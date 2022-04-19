package G6Shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import G6Shop.model.Image;

public interface ImageRepository extends CrudRepository<Image, Integer> {

    @Query("SELECT i FROM Image i WHERE i.name LIKE  %:name%")
    List<Image> findByName(String name);

    @Query("SELECT i.name FROM Image i")
    List<String> findAllByName();

}
