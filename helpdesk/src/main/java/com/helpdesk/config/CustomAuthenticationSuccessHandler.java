package com.helpdesk.config;

import com.helpdesk.model.User;
import com.helpdesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // Get the logged-in username
        String username = authentication.getName();
        
        // Find the user in the database
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = userRepository.findByEmail(username);
        }
        
        // Update the last login date
        if (user != null) {
            System.out.println("Updating last login date for user: " + username);
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            System.out.println("Last login date updated successfully");
        } else {
            System.out.println("User not found: " + username);
        }
        
        // Continue with the default success handler behavior
        super.onAuthenticationSuccess(request, response, authentication);
    }
}