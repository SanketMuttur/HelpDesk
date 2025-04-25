package com.helpdesk.controller;

import com.helpdesk.service.CommentService;
import com.helpdesk.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final PostService postService;
    private final CommentService commentService;

    @Autowired
    public ReportController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @PostMapping("/post/{id}")
    public ResponseEntity<String> reportPost(
            @PathVariable Long id,
            @RequestParam String reason,
            Authentication authentication) {
        
        postService.reportPost(id, reason, authentication.getName());
        return ResponseEntity.ok("Post reported successfully");
    }

    @PostMapping("/comment/{id}")
    public ResponseEntity<String> reportComment(
            @PathVariable Long id,
            @RequestParam String reason,
            Authentication authentication) {
        
        commentService.reportComment(id, reason, authentication.getName());
        return ResponseEntity.ok("Comment reported successfully");
    }
}