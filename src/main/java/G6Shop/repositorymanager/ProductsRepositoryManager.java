package G6Shop.repositorymanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import G6Shop.model.Products;
import G6Shop.model.Version.VersionName;

import java.util.*;

interface ProductsRepository extends CrudRepository<Products, Integer> {

    @Query("SELECT prod FROM Products prod where prod.name LIKE :name")
    List<Products> findProductByName(String name);

    @Query("SELECT prod FROM Products prod where prod.size LIKE :size")
    List<Products> findProductBySize(String size);

}

@Component
public class ProductsRepositoryManager extends AbstractRepositoryManager {

    public List<Products> findProductByName(String name) {
        return productsRepository.findProductByName(name);
    }
    
    public List<Products> findProductBySize(String size) {
    return productsRepository.findProductBySize(size);
    }

    @Autowired
    private ProductsRepository productsRepository;

    public Iterable<Products> findAll() {
        return productsRepository.findAll();
    }

    public Optional<Products> findById(int id) {
        return productsRepository.findById(id);
    }

    public void save(Products product) {
        productsRepository.save(product);
        super.updateVersion(VersionName.CONTACT);
    }

    public void deleteById(int id) {
        Optional<Products> optional = productsRepository.findById(id);
        if (optional.isPresent()) {
            Products prod = optional.get();
            fileLocationService.deleteImage(prod.getDrawablePath());
            productsRepository.deleteById(prod.getId());
        }
        updateVersion(VersionName.CONTACT);
    }

    public long getVersion() {
        return super.getVersion(VersionName.CONTACT);
    }

    public long count() {
        return productsRepository.count();
    }

}
