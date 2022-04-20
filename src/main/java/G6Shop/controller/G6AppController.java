package G6Shop.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import G6Shop.model.Holiday;
import G6Shop.model.Products;
import G6Shop.model.Version;
import G6Shop.model.Version.VersionName;
import G6Shop.repository.VersionRepository;
import G6Shop.repositorymanager.HolidayRepositoryManager;
import G6Shop.repositorymanager.ProductsRepositoryManager;





@RestController
public class G6AppController {

    @Value("${app.name:G6Shop}")
    private String appName;

    @Autowired
    ProductsRepositoryManager productsRepositoryManager;

    @Autowired
    private HolidayRepositoryManager holidayRepositoryManager;

    @Autowired
    ServletContext context;

    @Autowired
    VersionRepository versionsRepository;


    @GetMapping(value = "/api/products")
    public List<Products> getProducts() {
        Iterable<Products> iterable = productsRepositoryManager.findAll();
        Iterator<Products> iterator = iterable.iterator();
        List<Products> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }


    @GetMapping("/api/holidays")
    public List<Holiday> getHolidays() {
        Iterable<Holiday> iterable = holidayRepositoryManager.findAll();
        Iterator<Holiday> iterator = iterable.iterator();
        List<Holiday> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }

    @GetMapping("/api/holidays_version")
    public long getHolidaysVersion() {
        return holidayRepositoryManager.getVersion();
    }

    @GetMapping("/api/products_version")
    public long getProductsVersion() {
        return productsRepositoryManager.getVersion();
    }



    @GetMapping("/api/server_time")
    public String getServerTime() {
        return LocalDate.now().toString();
    }

    @GetMapping("/api/versions")
    public List<Version> getVersions() {
        List<Version> result = new ArrayList<>();
        for (VersionName vn : VersionName.values()) {
            List<Version> vs = versionsRepository.findByName(vn.toString());
            result.addAll(vs);
        }
        return result;
    }

}
