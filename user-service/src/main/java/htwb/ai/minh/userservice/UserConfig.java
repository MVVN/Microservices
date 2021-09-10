package htwb.ai.minh.userservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class UserConfig {
    @Bean
    CommandLineRunner commandLineRunner(UserRepository repository) {
        return args -> {
            User muster = new User("mmuster", "pass1234", "Maxim", "Muster");
            User schuler = new User("eschuler", "pass1234", "Elena", "Schuler");
            repository.saveAll(List.of(muster, schuler));
        };
    }
}
