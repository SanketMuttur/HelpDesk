package com.helpdesk.controller;

import com.helpdesk.dto.CommentDto;
import com.helpdesk.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentRestController {

    private final CommentService commentService;

    @Autowired
    public CommentRestController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/post/{postId}")
    public List<CommentDto> getCommentsByPostId(@PathVariable Long postId) {
        return commentService.getCommentsByPostId(postId);
    }

    @PostMapping
    public CommentDto createComment(@RequestBody CommentDto commentDto, Authentication authentication) {
        return commentService.createComment(commentDto, authentication.getName());
    }

    @PutMapping("/{id}")
    public CommentDto updateComment(@PathVariable Long id, @RequestBody CommentDto commentDto, 
                                   Authentication authentication) {
        CommentDto existingComment = commentService.getCommentById(id);
        
        // Check if the user is the author of the comment
        if (!existingComment.getAuthorUsername().equals(authentication.getName())) {
            throw new IllegalStateException("You can only edit your own comments");
        }
        
        return commentService.updateComment(id, commentDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id, Authentication authentication) {
        CommentDto existingComment = commentService.getCommentById(id);
        
        // Check if the user is the author of the comment
        if (!existingComment.getAuthorUsername().equals(authentication.getName())) {
            throw new IllegalStateException("You can only delete your own comments");
        }
        
        commentService.deleteComment(id);
        return ResponseEntity.ok("Comment deleted successfully");
    }
}