package G6Shop.repositorymanager;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import G6Shop.model.Produto;
import G6Shop.model.Version.VersionName;

interface ProdutoRepository extends CrudRepository<Produto, Integer> {
    // @Query("SELECT p FROM Products p where p.name LIKE :name")
    // List<Produto> findProductsByName(String name);
}

@Component
public class ProdutoRepositoryManager extends AbstractRepositoryManager {

    @Autowired
    private ProdutoRepository produtoRepository;

    public Iterable<Produto> findAll() {
        return produtoRepository.findAll();
    }

    public Optional<Produto> findById(int id) {
        return produtoRepository.findById(id);
    }

    public void save(Produto product) {
        produtoRepository.save(product);
        super.updateVersion(VersionName.PRODUCTS);
    }

    public void deleteById(int id) {
        produtoRepository.deleteById(id);
        super.updateVersion(VersionName.PRODUCTS);
    }

    public void deleteProductsById(int id) {
        Optional<Produto> optional = produtoRepository.findById(id);
        if (optional.isPresent()) {
            Produto product = optional.get();
            fileLocationService.deleteImage(product.getDrawablePath());
            produtoRepository.deleteById(id);
            super.updateVersion(VersionName.TRAINLINE);
        }
    }

    public long getVersion() {
        return super.getVersion(VersionName.PRODUCTS);
    }

    public long count() {
        return produtoRepository.count();
    }

    // public List<Produto> findProductsByDescription(String name) {
    //     return produtoRepository.findProductsByName(name);
    // }


    // public List<Products> findProductsWithSameOrderByName(int productOrder, int trainLineId) {
    //     return produtoRepository.findProductssWithSameOrderByName(productOrder, trainLineId);
    // }
}
