package com.helpdesk.config;

import com.helpdesk.model.User;
import com.helpdesk.repository.UserRepository;
import com.helpdesk.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        // Get the authenticated user's username
        Object principal = event.getAuthentication().getPrincipal();
        
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            System.out.println("Authentication success event for user: " + username);
            userService.updateLastLoginDate(username);
        } else {
            System.out.println("Principal is not a UserDetails instance: " + principal.getClass().getName());
        }
    }
}