package com.helpdesk.controller;

import com.helpdesk.dto.UserProfileDto;
import com.helpdesk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication authentication) {
        String username = authentication.getName();
        UserProfileDto userProfile = userService.getUserProfile(username);
        model.addAttribute("user", userProfile);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("user") UserProfileDto userProfileDto) {
        userService.updateUserProfile(userProfileDto);
        return "redirect:/user/profile?success";
    }
}