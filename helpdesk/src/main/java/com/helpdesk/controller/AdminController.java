package com.helpdesk.controller;

import com.helpdesk.dto.CommentDto;
import com.helpdesk.dto.PostDto;
import com.helpdesk.dto.UserDto;
import com.helpdesk.model.User;
import com.helpdesk.repository.UserRepository;
import com.helpdesk.service.CommentService;
import com.helpdesk.service.PostService;
import com.helpdesk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public AdminController(UserRepository userRepository, 
                          UserService userService,
                          PostService postService,
                          CommentService commentService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalPosts", postService.getPostCount());
        model.addAttribute("totalComments", commentService.getCommentCount());
        model.addAttribute("postReports", postService.getReportedPosts());
        model.addAttribute("commentReports", commentService.getReportedComments());
        return "admin/dashboard";
    }

    // User Management
    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @GetMapping("/users/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        UserDto user = userService.getUserById(id);
        List<PostDto> userPosts = postService.getPostsByUserForAdmin(id);
        List<CommentDto> userComments = commentService.getCommentsByUserForAdmin(id);
        
        model.addAttribute("user", user);
        model.addAttribute("userPosts", userPosts);
        model.addAttribute("userComments", userComments);
        
        return "admin/user-view";
    }
    
    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.toggleUserStatus(id);
        redirectAttributes.addFlashAttribute("success", "User status updated successfully");
        return "redirect:/admin/users";
    }
    
    @PostMapping("/users/{id}/change-role")
    public String changeUserRole(@PathVariable Long id, @RequestParam String role, RedirectAttributes redirectAttributes) {
        userService.changeUserRole(id, role);
        redirectAttributes.addFlashAttribute("success", "User role updated successfully");
        return "redirect:/admin/users/" + id;
    }
    
    // Add this new endpoint for banning users
    @PostMapping("/users/{id}/ban")
    public String banUser(@PathVariable Long id, @RequestParam String reason, RedirectAttributes redirectAttributes) {
        try {
            userService.banUser(id, reason);
            redirectAttributes.addFlashAttribute("successMessage", "User has been banned successfully.");
            return "redirect:/admin/users/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to ban user: " + e.getMessage());
            return "redirect:/admin/users/" + id;
        }
    }

    // Post Management
    @GetMapping("/posts")
    public String managePosts(Model model) {
        List<PostDto> posts = postService.getAllPostsForAdmin();
        model.addAttribute("posts", posts);
        return "admin/posts";
    }
    
    @GetMapping("/posts/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.getPostById(id));
        model.addAttribute("comments", commentService.getCommentsByPostId(id));
        return "admin/post-view";
    }
    
    @PostMapping("/posts/{id}/hide")
    public String hidePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        postService.togglePostVisibility(id);
        redirectAttributes.addFlashAttribute("success", "Post visibility updated successfully");
        return "redirect:/admin/posts";
    }
    
    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        postService.deletePost(id);
        redirectAttributes.addFlashAttribute("success", "Post deleted successfully");
        return "redirect:/admin/posts";
    }

    // Comment Management
    @GetMapping("/comments")
    public String listComments(Model model) {
        model.addAttribute("comments", commentService.getAllComments());
        return "admin/comments";
    }
    
    @PostMapping("/comments/{id}/hide")
    public String hideComment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        commentService.toggleCommentVisibility(id);
        redirectAttributes.addFlashAttribute("success", "Comment visibility updated successfully");
        return "redirect:/admin/comments";
    }
    
    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        commentService.deleteComment(id);
        redirectAttributes.addFlashAttribute("success", "Comment deleted successfully");
        return "redirect:/admin/comments";
    }
    
    // Reporting System
    @GetMapping("/reports")
    public String viewReports(Model model) {
        model.addAttribute("postReports", postService.getReportedPosts());
        model.addAttribute("commentReports", commentService.getReportedComments());
        return "admin/reports";
    }
    
    @PostMapping("/reports/post/{id}/resolve")
    public String resolvePostReport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        postService.resolveReport(id);
        redirectAttributes.addFlashAttribute("success", "Post report resolved successfully");
        return "redirect:/admin/reports";
    }
    
    @PostMapping("/reports/comment/{id}/resolve")
    public String resolveCommentReport(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        commentService.resolveReport(id);
        redirectAttributes.addFlashAttribute("success", "Comment report resolved successfully");
        return "redirect:/admin/reports";
    }
}