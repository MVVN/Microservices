package htwb.ai.minh.userservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Authentication Rest-Controller to Login
 */
@RestController
@RequestMapping(path = "/auth")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> login(@RequestBody @Valid User user) {
        Optional<User> userOptional;

        userOptional = userService.findById(user);

        if (userOptional.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.UNAUTHORIZED);
        }

        User foundUser = userOptional.get();

        if (!user.getPassword().equals(foundUser.getPassword())) {
            return new ResponseEntity<>("Wrong Password", HttpStatus.UNAUTHORIZED);
        }

        HttpHeaders header = new HttpHeaders();
        JWTgenerator jwTgenerator = new JWTgenerator();

        return ResponseEntity.ok().headers(header).body(jwTgenerator.generateToken(foundUser));
    }

}
