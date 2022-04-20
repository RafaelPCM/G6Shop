package G6Shop.controller;


import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import G6Shop.model.Contact;
import G6Shop.model.Holiday;
import G6Shop.model.ModelWithDrawablePath;
import G6Shop.model.Produto;
import G6Shop.model.User;
import G6Shop.repository.UserRepository;
import G6Shop.repositorymanager.ContactRepositoryManager;
import G6Shop.repositorymanager.HolidayRepositoryManager;
import G6Shop.repositorymanager.ProdutoRepositoryManager;




@Controller
@ControllerAdvice
public class G6WebController {

  private static final String CURRENT_USER = "currentUser";

  @Autowired
  private HolidayRepositoryManager holidayRepositoryManager;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private FileLocationService fileLocationService;

  @Autowired
  private ProdutoRepositoryManager produtoRepositoryManager;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private ContactRepositoryManager contactRepositoryManager;

  enum Status {
    NOT_FOUND("notfound"), ALTERED("altered"), CREATED("created"), DELETED("deleted"),
    ORDER_ALREADY_EXISTS("order_already_exists"), PRODUCT_NOT_FOUND("product_not_found"),
    USER_ALREADY_EXISTS("user_already_exists"), USER_PASSWORDS_DONT_MATCH("user_passwords_dont_match"),
    USER_NOT_FOUND("user_not_found");

    String name;

    Status(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  enum Page {
    TAX("tax"), HOLIDAYS("holidays"), TRAIN_LINES("train_lines"), NOTIFICATIONS("notifications"), USERS("users"),
    PRODUCTS("products"), TIME("time"), CONTACTS("contacts"), TIMETABLEIMAGE("timetable_image"),
    TRAIN_MAP_IMAGE("trainmap_image"), ALTERUSER("alteruser"), PRODUTO("produto");

    String name;

    Page(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  static final String STATUS = "status";
  static final String REDIRECT = "redirect:/";
  static final String NEW_ENTITY = "newEntity";

  private static final String buildRedirectWithStatusLink(Page page, Status status) {
    return REDIRECT + page.toString() + "?status=" + status.toString();
  }

  @GetMapping("/")
  public String index(Model model) {
    return "redirect:products";
  }

  @GetMapping("/login")
  public String login(Model model) throws IOException {
    return "login";
  }


  @GetMapping("/users")
  public String users(Model model, @ModelAttribute(STATUS) Object statusAttribute) {
    List<User> users = new ArrayList<>();
    User currentUser = getCurrentUser();
    if (currentUser.getRole().equals("ADMIN")) {
      Iterable<User> iterable = userRepository.findAll();
      Iterator<User> iterator = iterable.iterator();
      while (iterator.hasNext()) {

        User u = iterator.next();
        users.add(u);
      }
    } else {
      users.add(currentUser);
    }
    model.addAttribute(Page.USERS.toString(), users);
    model.addAttribute(CURRENT_USER, currentUser);
    if (statusAttribute instanceof Status) {
      model.addAttribute(STATUS, statusAttribute.toString());
    }

    return Page.USERS.toString();
  }

  private User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String currentPrincipalName = authentication.getName();
    return userRepository.findUsersWithSameName(currentPrincipalName).get(0);
  }


  @GetMapping("/produto")
  public String produto(Model model, @ModelAttribute(STATUS) Object statusAttribute) {
    List<Produto>produto = new ArrayList<>();
    User currentUser = getCurrentUser();
    if (currentUser.getRole() != null) {
      Iterable<Produto> iterable = produtoRepositoryManager.findAll();
      Iterator<Produto> iterator = iterable.iterator();
      while (iterator.hasNext()) {
        Produto s = iterator.next();
        produto.add(s);
      }
    }
    model.addAttribute(Page.PRODUTO.toString(), produto);
    model.addAttribute(CURRENT_USER, currentUser);
    if (statusAttribute instanceof Status) {
      model.addAttribute(STATUS, statusAttribute.toString());
    }

    return Page.PRODUTO.toString();
  }

  
  @GetMapping("/alterproduto")
  public String showAlterProdutoPage(Model model, RedirectAttributes attributes,
      @RequestParam(value = "id", required = false) Integer id) {
    Produto produto;
    if (id != null) {
      model.addAttribute(NEW_ENTITY, false);
      Optional<Produto> optionalCurrentContact = produtoRepositoryManager.findById(id);
      if (optionalCurrentContact.isPresent()) {
        produto = optionalCurrentContact.get();
      } else {
        produto = new Produto();
      }
    } else {
      model.addAttribute(NEW_ENTITY, true);
      produto = new Produto();
    }
    model.addAttribute("prod", produto);
    return "alterproduto";
  }

  @PostMapping("/alterproduto")
  @Transactional
  public RedirectView alterProduto(RedirectAttributes attributes, @RequestParam("id") Integer id,
      @RequestParam("name") String name,
      @RequestParam("size") String size,
      @RequestParam("price") Integer price
      ) {
    Optional<Produto> optionalCurrentProduto = produtoRepositoryManager.findById(id);

    if (optionalCurrentProduto.isPresent()) {
      var currentProduto = optionalCurrentProduto.get();
      currentProduto.setName(name);
      currentProduto.setSize(size);
      currentProduto.setPrice(price);

      produtoRepositoryManager.save(currentProduto);
    } else {
      var produto = new Produto();
      produto.setName(name);
      produto.setSize(size);
      produto.setPrice(price);

      produtoRepositoryManager.save(produto);
    }
    attributes.addFlashAttribute(STATUS, Status.ALTERED);
    return new RedirectView(Page.PRODUTO.toString());
  }

  @GetMapping("/deleteproduto")
  public String deleteProduto(RedirectAttributes attributes, @RequestParam("id") int id) {
    produtoRepositoryManager.deleteById(id);
    attributes.addFlashAttribute(STATUS, Status.DELETED);
    return REDIRECT + Page.PRODUTO.toString();
  }
  

  @GetMapping("/holidays")
  public String holidays(ModelMap model, @ModelAttribute(STATUS) Object statusAttribute) {
    List<Holiday> holiday = new ArrayList<>();
    User currentUser = getCurrentUser();
    if (currentUser.getRole() != null) {
      Iterable<Holiday> iterable = holidayRepositoryManager.findAll();
      Iterator<Holiday> iterator = iterable.iterator();
      while (iterator.hasNext()) {
        Holiday h = iterator.next();
        holiday.add(h);
      }
      model.addAttribute(Page.HOLIDAYS.toString(), holiday);
      model.addAttribute(CURRENT_USER, currentUser);

      if (statusAttribute instanceof Status) {
        model.addAttribute(STATUS, statusAttribute.toString());
      }
    }
    return Page.HOLIDAYS.toString();
  }

  @GetMapping("/alterholiday")
  public String showAlterHolidayPage(Model model, RedirectAttributes attributes,
      @RequestParam(value = "id", required = false) Integer id) {
    Holiday holiday;
    if (id != null) {
      model.addAttribute(NEW_ENTITY, false);
      Optional<Holiday> optionalCurrentContact = holidayRepositoryManager.findById(id);
      if (optionalCurrentContact.isPresent()) {
        holiday = optionalCurrentContact.get();
      } else {
        holiday = new Holiday();
      }
    } else {
      model.addAttribute(NEW_ENTITY, true);
      holiday = new Holiday();
    }
    model.addAttribute("hp", holiday);
    return "alterholiday";
  }

  @PostMapping("/alterholiday")
  @Transactional
  public RedirectView alterholiday(RedirectAttributes attributes, @RequestParam("id") Integer id,
      @RequestParam("description") String description,
      @RequestParam("localDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate localDate) {
    Optional<Holiday> optionalCurrentHoliday = holidayRepositoryManager.findById(id);

    if (optionalCurrentHoliday.isPresent()) {
      var currentHoliday = optionalCurrentHoliday.get();
      currentHoliday.setDescription(description);
      currentHoliday.setLocalDate(localDate);

      holidayRepositoryManager.save(currentHoliday);
    } else {
      var holiday = new Holiday();
      holiday.setDescription(description);
      holiday.setLocalDate(localDate);

      holidayRepositoryManager.save(holiday);
    }
    attributes.addFlashAttribute(STATUS, Status.ALTERED);
    return new RedirectView(Page.HOLIDAYS.toString());
  }

  @GetMapping("/deleteholiday")
  public RedirectView deleteHoliday(@RequestParam("id") Integer id, RedirectAttributes attributes) {
    holidayRepositoryManager.deleteById(id);
    attributes.addFlashAttribute(STATUS, Status.DELETED);
    return new RedirectView(Page.HOLIDAYS.toString());
  }


  @GetMapping("/switchuserstatus")
  @Transactional
  public String switchUserStatus(@RequestParam("id") int id) {
    Optional<User> optionalCurrentUser = userRepository.findById(id);
    if (optionalCurrentUser.isPresent()) {
      var currentUser = optionalCurrentUser.get();

      currentUser.setEnabled(!currentUser.getEnabled());

      userRepository.save(currentUser);
    }
    return buildRedirectWithStatusLink(Page.USERS, Status.ALTERED);
  }

  

  @GetMapping("/alteruser")
  public String showAlterUserPage(Model model, RedirectAttributes attributes,
      @RequestParam(value = "id", required = false) Integer id) {
    User user;
    User currentUser = getCurrentUser();

    if (id != null) {
      model.addAttribute(NEW_ENTITY, false);
      Optional<User> optionalCurrentUser = userRepository.findById(id);
      if (optionalCurrentUser.isPresent()) {
        user = optionalCurrentUser.get();
      } else {
        user = new User();
      }
    } else {
      model.addAttribute(NEW_ENTITY, true);
      user = new User();
    }

    model.addAttribute("us", user);
    model.addAttribute(CURRENT_USER, currentUser);
    return "alteruser";
  }

  @PostMapping("/alteruser")
  @Transactional
  public RedirectView alterUser(RedirectAttributes attributes, @RequestParam("id") int id,
      @RequestParam("username") String username, @RequestParam("registrationNumber") String registrationNumber,
      @RequestParam(value = "c1", required = false) String updatePassword, @RequestParam("password") String password,
      @RequestParam(value = "new_password", required = false) String newPassword, @RequestParam("role") String role)
      throws IllegalStateException {
    Optional<User> optionalCurrentUser = userRepository.findById(id);
    User user;
    if (optionalCurrentUser.isPresent()) {
      user = optionalCurrentUser.get();
      boolean result = passwordEncoder.matches(password, user.getPassword());
      if (user.getUsername().equals(username) || userRepository.findUsersWithSameName(username).isEmpty()) {
        if (result) {
          user.setRegistrationNumber(registrationNumber);
          user.setUsername(username);
          if (updatePassword != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
          }
          user.setRole(role);
          user.setEnabled(true);

          userRepository.save(user);
          attributes.addFlashAttribute(STATUS, Status.ALTERED);
        } else {
          attributes.addFlashAttribute(STATUS, Status.USER_PASSWORDS_DONT_MATCH);
        }
      } else {
        attributes.addFlashAttribute(STATUS, Status.USER_ALREADY_EXISTS);
      }
    } else {
      attributes.addFlashAttribute(STATUS, Status.USER_NOT_FOUND);
    }
    return new RedirectView(Page.USERS.toString());
  }

  @GetMapping("/createuser")
  public String showCreateUser(Model model, RedirectAttributes attributes,
      @RequestParam(value = "id", required = false) Integer id) {
    User user;
    if (id != null) {
      model.addAttribute(NEW_ENTITY, false);
      Optional<User> optionalCurrentUser = userRepository.findById(id);
      if (optionalCurrentUser.isPresent()) {
        user = optionalCurrentUser.get();
      } else {
        user = new User();
      }
    } else {
      model.addAttribute(NEW_ENTITY, true);
      user = new User();
    }

    model.addAttribute("us", user);
    return "createuser";
  }

  @PostMapping("/createuser")
  @Transactional
  public String createUser(RedirectAttributes attributes, @RequestParam("username") String username,
      @RequestParam("password") String password, @RequestParam("registrationNumber") String registrationNumber,
      @RequestParam("role") String role) {
    var user = new User();

    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password));
    user.setRegistrationNumber(registrationNumber);
    user.setEnabled(true);
    user.setRole(role);
    userRepository.save(user);

    attributes.addFlashAttribute(STATUS, Status.CREATED);
    return REDIRECT + Page.USERS.toString();
  }

  @GetMapping("/deleteuser")
  public String deleteUser(RedirectAttributes attributes, @RequestParam("id") int id) {
    userRepository.deleteById(id);
    attributes.addFlashAttribute(STATUS, Status.DELETED);
    return REDIRECT + Page.USERS.toString();
  }


  private void updateDrawablePath(ModelWithDrawablePath modelWithDrawablePath, String noImageCheckBox,
      MultipartFile file) throws IOException {
    if (noImageCheckBox == null) {
      var originalName = file.getOriginalFilename();
      if (originalName != null && !originalName.isEmpty()) {
        String newFileName = fileLocationService.saveImage(file.getBytes(), file.getOriginalFilename());
        modelWithDrawablePath.setDrawablePath(newFileName);
      }
    } else {
      fileLocationService.deleteImage(modelWithDrawablePath.getDrawablePath());
      modelWithDrawablePath.setDrawablePath("");
    }
  }




  @GetMapping("/contacts")
  public String contact(Model model, @ModelAttribute(STATUS) Object statusAttribute) {
    List<Contact> contacts = new ArrayList<>();
    User currentUser = getCurrentUser();
    if (currentUser.getRole() != null) {
      Iterable<Contact> iterable = contactRepositoryManager.findAll();
      Iterator<Contact> iterator = iterable.iterator();
      while (iterator.hasNext()) {
        Contact c = iterator.next();
        contacts.add(c);
      }
    }
    model.addAttribute(Page.CONTACTS.toString(), contacts);
    model.addAttribute(CURRENT_USER, currentUser);
    if (statusAttribute instanceof Status) {
      model.addAttribute(STATUS, statusAttribute.toString());
    }

    return Page.CONTACTS.toString();
  }


  @GetMapping("/altercontact")
  public String showAlterContactPage(Model model, RedirectAttributes attributes,
      @RequestParam(value = "id", required = false) Integer id) {
    Contact contact;
    if (id != null) {
      model.addAttribute(NEW_ENTITY, false);
      Optional<Contact> optionalCurrentContact = contactRepositoryManager.findById(id);
      if (optionalCurrentContact.isPresent()) {
        contact = optionalCurrentContact.get();
      } else {
        contact = new Contact();
      }
    } else {
      model.addAttribute(NEW_ENTITY, true);
      contact = new Contact();
    }
    model.addAttribute("contact", contact);
    return "altercontact";
  }


  @PostMapping("/altercontact")
  @Transactional
  public RedirectView altercontact(RedirectAttributes attributes, @RequestParam("id") Integer id,
      @RequestParam("name") String name, @RequestParam("size") String size,
      @RequestParam("price") Integer price,
      @RequestParam(value = "no_image_checkbox", required = false) String noImageCheckBox,
      @RequestParam(value = "file", required = false) MultipartFile file) throws IllegalStateException, IOException {

    Optional<Contact> optionalCurrentContact = contactRepositoryManager.findById(id);

    Contact contact;
    if (optionalCurrentContact.isPresent()) {
      contact = optionalCurrentContact.get();
    } else {
      contact = new Contact();
    }
    contact.setName(name);
    contact.setSize(size);
    contact.setPrice(price);
    updateDrawablePath(contact, noImageCheckBox, file);

    contactRepositoryManager.save(contact);

    attributes.addFlashAttribute(STATUS, Status.ALTERED);
    return new RedirectView(Page.CONTACTS.toString());
  }

  @GetMapping("/deletecontact")
  public String deleteContact(@RequestParam("id") Integer id, RedirectAttributes attributes) {
    try {
      contactRepositoryManager.deleteById(id);
      attributes.addFlashAttribute(STATUS, Status.DELETED);
    } catch (Exception e) {
      attributes.addFlashAttribute(STATUS, Status.NOT_FOUND);
    }
    return REDIRECT + Page.CONTACTS.toString();
  }






}
