package G6Shop.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import G6Shop.model.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {

    @Query("SELECT user FROM User user where user.username = :username")
    List<User> findUsersWithSameName(String username);

}
