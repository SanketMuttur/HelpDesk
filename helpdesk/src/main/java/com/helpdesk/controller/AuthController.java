package com.helpdesk.controller;

import com.helpdesk.dto.UserRegistrationDto;
import com.helpdesk.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/registration")
    public String registerUserAccount(
            @ModelAttribute("user") @Valid UserRegistrationDto userDto,
            BindingResult result,
            Model model) {

        // Check if passwords match
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", null, "Passwords do not match");
        }

        // Check if user already exists
        if (userService.checkIfUserExists(userDto.getEmail(), userDto.getUsername())) {
            result.rejectValue("email", null, "There is already an account registered with that email or username");
        }

        if (result.hasErrors()) {
            return "register";
        }

        userService.save(userDto);
        return "redirect:/registration?success";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
}