package com.helpdesk.config;

import com.helpdesk.model.User;
import com.helpdesk.repository.UserRepository;
import com.helpdesk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    public CustomAuthenticationProvider(UserService userService, PasswordEncoder passwordEncoder) {
        this.setUserDetailsService(userService);
        this.setPasswordEncoder(passwordEncoder);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            // First, let the parent class handle the username/password validation
            Authentication result = super.authenticate(authentication);
            
            // If we get here, the password was correct
            // Now check the account status
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            if (user == null) {
                user = userRepository.findByEmail(username);
            }
            
            if (user != null) {
                // Check if user is banned
                if ("BANNED".equals(user.getAccountStatus())) {
                    throw new LockedException("Your account has been banned. Reason: " + user.getSuspensionReason());
                }

                // Check if user is suspended
                if ("SUSPENDED".equals(user.getAccountStatus())) {
                    // Check if suspension period has ended
                    if (user.getSuspensionEndDate() != null && user.getSuspensionEndDate().after(new Date())) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        throw new LockedException("Your account is suspended until " + 
                            dateFormat.format(user.getSuspensionEndDate()) + 
                            ". Reason: " + user.getSuspensionReason());
                    } else {
                        // If suspension period has ended, reactivate the account
                        user.setAccountStatus("ACTIVE");
                        user.setSuspensionReason(null);
                        user.setSuspensionEndDate(null);
                        userRepository.save(user);
                    }
                }
            }
            
            return result;
        } catch (BadCredentialsException e) {
            // Rethrow bad credentials exception without checking account status
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}