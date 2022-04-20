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

import G6Shop.model.Products;
import G6Shop.model.Version;
import G6Shop.model.Version.VersionName;
import G6Shop.repository.VersionRepository;
import G6Shop.repositorymanager.ProductsRepositoryManager;

@RestController
public class G6AppController {

    @Value("${app.name:G6Shop}")
    private String appName;

    @Autowired
    ServletContext context;

    @Autowired
    VersionRepository versionsRepository;
    
    @Autowired
    private ProductsRepositoryManager productRepositoryManager;


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
    

    @GetMapping(value = "/api/product")
    public List<Products> productInJson() {
        Iterable<Products> iterable = productRepositoryManager.findAll();
        Iterator<Products> iterator = iterable.iterator();
        List<Products> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        return result;
    }


    @GetMapping("/api/product_version")
    public long getProductVersion() {
        return productRepositoryManager.getVersion();
    }


}
