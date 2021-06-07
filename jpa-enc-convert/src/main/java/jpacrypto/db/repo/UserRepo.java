package jpacrypto.db.repo;

import jpacrypto.db.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends CrudRepository<User, String> {

    User findByUserId(String userId);



}
