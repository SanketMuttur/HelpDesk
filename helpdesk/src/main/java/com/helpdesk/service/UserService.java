package com.helpdesk.service;

import com.helpdesk.dto.UserDto;
import com.helpdesk.dto.UserProfileDto;
import com.helpdesk.dto.UserRegistrationDto;
import com.helpdesk.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User save(UserRegistrationDto registrationDto);
    User findByUsername(String username);
    User findByEmail(String email);
    UserProfileDto getUserProfile(String username);
    void updateUserProfile(UserProfileDto userProfileDto);
    boolean checkIfUserExists(String email, String username);

    // New methods for admin moderation
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    void toggleUserStatus(Long id);
    void changeUserRole(Long id, String role);
    void suspendUser(Long id, String reason, int days);
    void banUser(Long id, String reason);
    void activateUser(Long id);

    // Add this method to the interface
    void updateLastLoginDate(String username);
}