package com.helpdesk.controller;

import com.helpdesk.dto.CommentDto;
import com.helpdesk.dto.PostDto;
import com.helpdesk.service.CategoryService;
import com.helpdesk.service.PostService;
import com.helpdesk.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CategoryService categoryService;
    private final CommentService commentService;

    @Autowired
    public PostController(PostService postService, CategoryService categoryService, CommentService commentService) {
        this.postService = postService;
        this.categoryService = categoryService;
        this.commentService = commentService;
    }

    @GetMapping
    public String getAllPosts(Model model) {
        List<PostDto> posts = postService.getAllPosts();
        model.addAttribute("posts", posts);
        return "posts/list";
    }

    // Update the viewPost method to include user's vote information
    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        PostDto post = postService.getPostWithUserVote(id, username);
        model.addAttribute("post", post);
        
        // Get comments with user's vote information
        List<CommentDto> comments = commentService.getCommentsWithUserVote(id, username);
        model.addAttribute("comments", comments);
        
        return "posts/view";
    }

    @GetMapping("/create")
    public String createPostForm(Model model) {
        model.addAttribute("post", new PostDto());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "posts/create";
    }

    @PostMapping("/create")
    public String createPost(@ModelAttribute("post") PostDto postDto, Authentication authentication) {
        String username = authentication.getName();
        postService.createPost(postDto, username);
        return "redirect:/posts";
    }

    @GetMapping("/edit/{id}")
    public String editPostForm(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.getPostById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "posts/edit";
    }

    @PostMapping("/edit/{id}")
    public String updatePost(@PathVariable Long id, @ModelAttribute("post") PostDto postDto) {
        postService.updatePost(id, postDto);
        return "redirect:/posts/" + id;
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }

    @GetMapping("/category/{id}")
    public String getPostsByCategory(@PathVariable Long id, Model model) {
        model.addAttribute("posts", postService.getPostsByCategory(id));
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "posts/by-category";
    }

    @GetMapping("/tag/{tag}")
    public String getPostsByTag(@PathVariable String tag, Model model) {
        model.addAttribute("posts", postService.getPostsByTag(tag));
        model.addAttribute("tag", tag);
        return "posts/by-tag";
    }

    @GetMapping("/search")
    public String searchPosts(@RequestParam String keyword, Model model) {
        model.addAttribute("posts", postService.searchPosts(keyword));
        model.addAttribute("keyword", keyword);
        return "posts/search-results";
    }
}