package edu.tcu.cs.backend.system;

import edu.tcu.cs.backend.user.User;
import edu.tcu.cs.backend.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Profile("dev") // Only runs in dev environment
public class DBDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DBDataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        // If the ID is auto generated, don't manually set the ID
        User user1 = new User();
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john.doe@example.com");
        user1.setPhoneNumber("1234567890");
        user1.setPassword("P@ssw0rd");
        user1.setRole("ADMIN");
        user1.setPosition(List.of("Director", "Producer"));

        User user2 = new User();
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setEmail("jane.smith@example.com");
        user2.setPhoneNumber("9876543210");
        user2.setPassword("EncodedP@assWord");
        user2.setRole("CREW MEMBER");
        user2.setPosition(List.of("Camera", "Photographer"));

        User user3 = new User();
        user3.setFirstName("Bob");
        user3.setLastName("Marley");
        user3.setEmail("bob.marley@example.com");
        user3.setPhoneNumber("1231231234");
        user3.setPassword("P@ssw0rd");
        user3.setRole("CREW MEMBER");
        user3.setPosition(List.of("Log", "Photographer"));

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
    }

}
