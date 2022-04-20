package G6Shop.repositorymanager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import G6Shop.model.Products;
import G6Shop.model.Version.VersionName;

import java.util.*;

interface ContactRepository extends CrudRepository<Products, Integer> {

    @Query("SELECT prod FROM Products prod where prod.name LIKE :name")
    List<Products> findProductByName(String name);

    @Query("SELECT prod FROM Products prod where prod.size LIKE :size")
    List<Products> findProductBySize(String size);

}

@Component
public class ProductsRepositoryManager extends AbstractRepositoryManager {

    public List<Products> findProductByName(String name) {
        return contactRepository.findProductByName(name);
    }
    
    public List<Products> findProductBySize(String size) {
    return contactRepository.findProductBySize(size);
    }

    @Autowired
    private ContactRepository contactRepository;

    public Iterable<Products> findAll() {
        return contactRepository.findAll();
    }

    public Optional<Products> findById(int id) {
        return contactRepository.findById(id);
    }

    public void save(Products contact) {
        contactRepository.save(contact);
        super.updateVersion(VersionName.CONTACT);
    }

    public void deleteById(int id) {
        Optional<Products> optional = contactRepository.findById(id);
        if (optional.isPresent()) {
            Products c = optional.get();
            fileLocationService.deleteImage(c.getDrawablePath());
            contactRepository.deleteById(c.getId());
        }
        updateVersion(VersionName.CONTACT);
    }

    public long getVersion() {
        return super.getVersion(VersionName.CONTACT);
    }

    public long count() {
        return contactRepository.count();
    }

}
