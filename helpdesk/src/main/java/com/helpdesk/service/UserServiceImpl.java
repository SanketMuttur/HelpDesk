package com.helpdesk.service;

import com.helpdesk.dto.UserDto;
import com.helpdesk.dto.UserProfileDto;
import com.helpdesk.dto.UserRegistrationDto;
import com.helpdesk.model.Role;
import com.helpdesk.model.User;
import com.helpdesk.repository.UserRepository;
import com.helpdesk.repository.PostRepository;
import com.helpdesk.repository.CommentRepository;
import com.helpdesk.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, 
                          PasswordEncoder passwordEncoder,
                          PostRepository postRepository,
                          CommentRepository commentRepository,
                          RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public User save(UserRegistrationDto registrationDto) {
        User user = new User();
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        
        // First save the user without roles
        userRepository.save(user);
        
        // Find existing ROLE_USER from database
        Role userRole = userRepository.findAll().stream()
                .filter(u -> u.getRoles() != null && !u.getRoles().isEmpty())
                .flatMap(u -> u.getRoles().stream())
                .filter(role -> "ROLE_USER".equals(role.getName()))
                .findFirst()
                .orElse(new Role("ROLE_USER"));
        
        // Create a mutable list for roles instead of using Arrays.asList
        ArrayList<Role> rolesList = new ArrayList<>();
        rolesList.add(userRole);
        
        // Set roles to user
        user.setRoles(rolesList);
        
        // Update user with roles
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = userRepository.findByEmail(username);
            if (user == null) {
                throw new UsernameNotFoundException("Bad Credentials");
            }
        }
        
        // Return a standard UserDetails object with just the username and password
        // This will be used only for password validation
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            mapRolesToAuthorities(user.getRoles())
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserProfileDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        
        UserProfileDto profileDto = new UserProfileDto();
        profileDto.setId(user.getId());
        profileDto.setFirstName(user.getFirstName());
        profileDto.setLastName(user.getLastName());
        profileDto.setUsername(user.getUsername());
        profileDto.setEmail(user.getEmail());
        
        return profileDto;
    }

    @Override
    public void updateUserProfile(UserProfileDto userProfileDto) {
        User user = userRepository.findById(userProfileDto.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        user.setFirstName(userProfileDto.getFirstName());
        user.setLastName(userProfileDto.getLastName());
        user.setEmail(userProfileDto.getEmail());
        
        userRepository.save(user);
    }

    @Override
    public boolean checkIfUserExists(String email, String username) {
        return userRepository.existsByEmail(email) || userRepository.existsByUsername(username);
    }

    // Add these methods to your UserServiceImpl class

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return convertToDto(user);
    }

    @Override
    @Transactional
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        if ("ACTIVE".equals(user.getAccountStatus())) {
            user.setAccountStatus("SUSPENDED");
            user.setSuspensionReason("Suspended by admin");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 30); // Default 30 days suspension
            user.setSuspensionEndDate(calendar.getTime());
        } else {
            user.setAccountStatus("ACTIVE");
            user.setSuspensionReason(null);
            user.setSuspensionEndDate(null);
        }
        
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changeUserRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        // Clear existing roles
        user.getRoles().clear();
        
        // Find existing roles instead of creating new ones
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER not found in database"));
        
        // Add the ROLE_USER role
        user.getRoles().add(userRole);
        
        // Add ROLE_ADMIN if needed
        if ("ADMIN".equals(role)) {
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not found in database"));
            user.getRoles().add(adminRole);
        }
        
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void suspendUser(Long id, String reason, int days) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        user.setAccountStatus("SUSPENDED");
        user.setSuspensionReason(reason);
        
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        user.setSuspensionEndDate(calendar.getTime());
        
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void banUser(Long id, String reason) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        user.setAccountStatus("BANNED");
        user.setSuspensionReason(reason);
        user.setSuspensionEndDate(null); // No end date for ban
        
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        
        user.setAccountStatus("ACTIVE");
        user.setSuspensionReason(null);
        user.setSuspensionEndDate(null);
        
        userRepository.save(user);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setAccountStatus(user.getAccountStatus());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setLastLoginDate(user.getLastLoginDate());
        dto.setSuspensionReason(user.getSuspensionReason());
        dto.setSuspensionEndDate(user.getSuspensionEndDate());
        
        // Get roles
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
        dto.setRoles(roles);
        
        // Get post and comment counts
        dto.setPostCount((int) postRepository.countByAuthor(user));
        dto.setCommentCount((int) commentRepository.countByAuthor(user));
        
        return dto;
    }

    @Override
    @Transactional
    public void updateLastLoginDate(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = userRepository.findByEmail(username);
        }
        
        if (user != null) {
            System.out.println("Service: Updating last login date for user: " + username);
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            System.out.println("Service: Last login date updated successfully for user ID: " + user.getId());
        }
    }
}