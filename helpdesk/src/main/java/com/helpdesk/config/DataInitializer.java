package com.helpdesk.config;

import com.helpdesk.model.Role;
import com.helpdesk.model.User;
import com.helpdesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Check if admin user exists
        if (userRepository.findByUsername("admin") == null) {
            // Create admin user
            User adminUser = new User();
            adminUser.setFirstName("Admin");
            adminUser.setLastName("User");
            adminUser.setUsername("admin");
            adminUser.setEmail("admin@helpdesk.com");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setRoles(Arrays.asList(
                new Role("ROLE_ADMIN"),
                new Role("ROLE_USER")
            ));
            
            userRepository.save(adminUser);
            System.out.println("Admin user created successfully!");
        }
    }
}