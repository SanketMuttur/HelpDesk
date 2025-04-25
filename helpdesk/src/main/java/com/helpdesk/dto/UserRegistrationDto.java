package com.helpdesk.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDto {
    
    @NotEmpty(message = "First name cannot be empty")
    private String firstName;
    
    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;
    
    @NotEmpty(message = "Username cannot be empty")
    @Size(min = 4, message = "Username must be at least 4 characters long")
    private String username;
    
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    private String email;
    
    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
    
    @NotEmpty(message = "Confirm password cannot be empty")
    private String confirmPassword;
}