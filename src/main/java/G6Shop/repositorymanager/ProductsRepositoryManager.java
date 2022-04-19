package G6Shop.repositorymanager;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import G6Shop.model.Products;
import G6Shop.model.Version.VersionName;

interface ProductsRepository extends CrudRepository<Products, Integer> {
    @Query("SELECT p FROM Products p where p.name LIKE :name")
    List<Products> findProductsByName(String name);
}

@Component
public class ProductsRepositoryManager extends AbstractRepositoryManager {

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
        super.updateVersion(VersionName.PRODUCTS);
    }

    public void deleteById(int id) {
        productsRepository.deleteById(id);
        super.updateVersion(VersionName.PRODUCTS);
    }

    public void deleteProductsById(int id) {
        Optional<Products> optional = productsRepository.findById(id);
        if (optional.isPresent()) {
            Products product = optional.get();
            fileLocationService.deleteImage(product.getDrawablePath());
            productsRepository.deleteById(id);
            super.updateVersion(VersionName.TRAINLINE);
        }
    }

    public long getVersion() {
        return super.getVersion(VersionName.PRODUCTS);
    }

    public long count() {
        return productsRepository.count();
    }

    public List<Products> findProductsByDescription(String name) {
        return productsRepository.findProductsByName(name);
    }


    // public List<Products> findProductsWithSameOrderByName(int productOrder, int trainLineId) {
    //     return productsRepository.findProductssWithSameOrderByName(productOrder, trainLineId);
    // }
}
