package G6Shop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import G6Shop.model.Version;

@Repository
public interface VersionRepository extends CrudRepository<Version, Integer> {

    @Query("SELECT i FROM Version i WHERE i.name LIKE :name")
    List<Version> findByName(String name);

    @Query("SELECT i FROM Version i WHERE i.name LIKE :name AND i.elementId = :elementId")
    List<Version> findByData(String name, int elementId);

}