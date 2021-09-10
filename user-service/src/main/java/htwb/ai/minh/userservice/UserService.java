package htwb.ai.minh.userservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service Layer to communicate with Data-Access-Layer
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserRepository getUserRepository() {
        return userRepository;
    }

    ////// Services //////

    public Optional<User> findById(User user) {
        return userRepository.findById(user.getUserId());
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

}
