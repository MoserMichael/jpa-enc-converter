package jpacrypto;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.Test;
import jpacrypto.db.repo.UserRepo;
import jpacrypto.db.entity.UserDetails;
import jpacrypto.db.entity.User;
import static org.junit.jupiter.api.Assertions.assertTrue;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Svc.class)
@ActiveProfiles(profiles = {"test"})
@DirtiesContext
@SpringBootTest(properties = {

})
public class SvcTest {

    @Autowired
    UserRepo usersRepo;

    public SvcTest() {
    }

    @Test
    public void createUserTest(){
        UserDetails userDetails;
        User user;

        for(int i = 0; i < 100; ++i) {
            String userId = Integer.toString(i);
 
            user = new User();
            user.setUserId(userId);

            userDetails = new UserDetails();
            userDetails.setUserId(userId);
            userDetails.setFirstName("Donald"+i);
            userDetails.setLastName("Duck"+i);

            user.setUserDetails(userDetails);

            usersRepo.save(user);
        }

        for(int i = 0; i < 100; ++i) {
            String userId = Integer.toString(i);

            user = usersRepo.findByUserId(userId);
            userDetails = user.getUserDetails();

            assertTrue(userDetails.getFirstName().equals("Donald"+i));
            assertTrue(userDetails.getLastName().equals("Duck"+i));

            usersRepo.delete(user);
        }



    }
 
}
