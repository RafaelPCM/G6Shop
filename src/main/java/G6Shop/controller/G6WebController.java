package G6Shop.controller;


import java.io.IOException;
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
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import G6Shop.model.Products;
import G6Shop.model.ModelWithDrawablePath;
import G6Shop.model.User;
import G6Shop.repository.UserRepository;
import G6Shop.repositorymanager.ProductsRepositoryManager;

@Controller
@ControllerAdvice
public class G6WebController {

  private static final String CURRENT_USER = "currentUser";

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private FileLocationService fileLocationService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private ProductsRepositoryManager productRepositoryManager;

  enum Status {
    NOT_FOUND("notfound"), ALTERED("altered"), CREATED("created"), DELETED("deleted"),
    ORDER_ALREADY_EXISTS("order_already_exists"), PRODUCT_NOT_FOUND("product_not_found"),
    USER_ALREADY_EXISTS("user_already_exists"), USER_PASSWORDS_DONT_MATCH("user_passwords_dont_match"),
    USER_NOT_FOUND("user_not_found"), OUT_OF_STOCK("outofstock");

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
    PRODUCTS("products"),USERS("users"), ALTERUSER("alteruser"), PRODUTO("produto"),
    LOGIN("login"), HOME("home"), BUYPRODUCT("buyproduct"), SHOPPINGCART("shoppingcart");

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
    return "redirect:home";
  }

  @GetMapping("/home")
  public String home(Model model) throws IOException {
    return "home";
  }

  @GetMapping("/login")
  public String login(Model model) throws IOException {
    return "login";
  }

  @GetMapping("/buyproduct")
  public String buyProduct(Model model) throws IOException {
    return "buyproduct";
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

  @GetMapping("/admincreateuser")
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
    return "admincreateuser";
  }

  @PostMapping("/admincreateuser")
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

  @GetMapping("/usercreateuser")
  public String showUserCreateUser(Model model, RedirectAttributes attributes,
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
    return "usercreateuser";
  }

  @PostMapping("/usercreateuser")
  @Transactional
  public String userCreateUser(RedirectAttributes attributes, @RequestParam("username") String username,
      @RequestParam("password") String password, @RequestParam("registrationNumber") String registrationNumber,
      @RequestParam("role") String role) {
    var user = new User();

    user.setUsername(username);
    user.setPassword(passwordEncoder.encode(password));
    user.setRegistrationNumber(registrationNumber);
    user.setEnabled(true);
    user.setRole(role);
    userRepository.save(user);

    return REDIRECT + Page.LOGIN.toString();
  }

  @GetMapping("/deleteuser")
  public String deleteUser(RedirectAttributes attributes, @RequestParam("id") int id) {
    userRepository.deleteById(id);
    attributes.addFlashAttribute(STATUS, Status.DELETED);
    return REDIRECT + Page.HOME.toString();
  }


  @GetMapping("/products")
  public String products(Model model, @ModelAttribute(STATUS) Object statusAttribute) {
    List<Products> products = new ArrayList<>();
    User currentUser = getCurrentUser();
    if (currentUser.getRole() != null) {
      Iterable<Products> iterable = productRepositoryManager.findAll();
      Iterator<Products> iterator = iterable.iterator();
      while (iterator.hasNext()) {
        Products prod = iterator.next();
        products.add(prod);
      }
    }
    model.addAttribute(Page.PRODUCTS.toString(), products);
    model.addAttribute(CURRENT_USER, currentUser);
    if (statusAttribute instanceof Status) {
      model.addAttribute(STATUS, statusAttribute.toString());
    }

    return Page.PRODUCTS.toString();
  }


  @GetMapping("/alterproduct")
  public String showAlterProductPage(Model model, RedirectAttributes attributes,
      @RequestParam(value = "id", required = false) Integer id) {
    Products product;
    if (id != null) {
      model.addAttribute(NEW_ENTITY, false);
      Optional<Products> optionalCurrentProduct = productRepositoryManager.findById(id);
      if (optionalCurrentProduct.isPresent()) {
        product = optionalCurrentProduct.get();
      } else {
        product = new Products();
      }
    } else {
      model.addAttribute(NEW_ENTITY, true);
      product = new Products();
    }
    model.addAttribute("product", product);
    return "alterproduct";
  }


  @PostMapping("/alterproduct")
  @Transactional
  public RedirectView alterProduct(RedirectAttributes attributes, @RequestParam("id") Integer id,
      @RequestParam("name") String name,
      @RequestParam("size") String size,
      @RequestParam("price") Integer price,
      @RequestParam(value = "no_image_checkbox", required = false) String noImageCheckBox,
      @RequestParam(value = "file", required = false) MultipartFile file,
      @RequestParam(value = "stock", required = false) Integer stock) throws IllegalStateException, IOException {

    Optional<Products> optionalCurrentProduct = productRepositoryManager.findById(id);

    Products product;
    if (optionalCurrentProduct.isPresent()) {
      product = optionalCurrentProduct.get();
    } else {
      product = new Products();
    }
    product.setName(name);
    product.setSize(size);
    product.setPrice(price);
    updateDrawablePath(product, noImageCheckBox, file);
    product.setStock(stock);

    productRepositoryManager.save(product);

    attributes.addFlashAttribute(STATUS, Status.ALTERED);
    return new RedirectView(Page.PRODUCTS.toString());
  }

  @GetMapping("/deleteproduct")
  public String deleteProduct(@RequestParam("id") Integer id, RedirectAttributes attributes) {
    try {
      productRepositoryManager.deleteById(id);
      attributes.addFlashAttribute(STATUS, Status.DELETED);
    } catch (Exception e) {
      attributes.addFlashAttribute(STATUS, Status.NOT_FOUND);
    }
    return REDIRECT + Page.PRODUCTS.toString();
  }


  @GetMapping("/shoppingcart")
  public String shoppingCart (Model model, RedirectAttributes attributes,
    @RequestParam(value = "id", required = false) Integer id) {
    Products product;
    if (id != null) {
      model.addAttribute(NEW_ENTITY, false);
      Optional<Products> optionalCurrentProduct = productRepositoryManager.findById(id);
      if (optionalCurrentProduct.isPresent()) {
        product = optionalCurrentProduct.get();
      } else {
        product = new Products();
      }
    } else {
      model.addAttribute(NEW_ENTITY, true);
      product = new Products();
    }
    model.addAttribute("product", product);

    return "shoppingcart";
  }

  @PostMapping("/shoppingcart")
  @Transactional
  public RedirectView shoppingCart(RedirectAttributes attributes, @RequestParam(value = "id", required = false) Integer id,
    @RequestParam(value = "name", required = false) String name,
    @RequestParam(value = "size", required = false) String size,
    @RequestParam(value = "price", required = false) Integer price,
    @RequestParam(value = "file", required = false) MultipartFile file,
    @RequestParam(value = "stock", required = false) Integer stock,
    @RequestParam(value = "buystock", required = true) Integer buystock) throws IllegalStateException, IOException {

    Optional<Products> optionalCurrentProduct = productRepositoryManager.findById(id);

    Products product;
    
    if (optionalCurrentProduct.isPresent()) {
      product = optionalCurrentProduct.get();
    } else {
      product = new Products();
    }
    product.setName(name);
    product.setSize(size);
    product.setPrice(price);
    product.setBuystock(buystock);
    //logica: Pegar o valor que foi inserido no carrinho (BuyStock) e subtrair do total do stock
    Integer result = product.getStock() - product.getBuystock();
    if(result < 0) {
      attributes.addFlashAttribute(STATUS, Status.OUT_OF_STOCK);
      return new RedirectView(Page.PRODUCTS.toString());
    } else {
      product.setStock(result);
    }

    productRepositoryManager.save(product);

    return new RedirectView(Page.BUYPRODUCT.toString());
  }

}
