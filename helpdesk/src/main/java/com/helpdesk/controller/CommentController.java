package com.helpdesk.controller;

import com.helpdesk.dto.CommentDto;
import com.helpdesk.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // Update the getCommentsByPostId method to include user's vote information
    // Update the endpoint to use a method that filters hidden comments
    @GetMapping("/post/{postId}")
    public List<CommentDto> getCommentsByPost(@PathVariable Long postId, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        // Use getCommentsByPost which will filter out hidden comments
        return commentService.getCommentsWithUserVote(postId, username);
    }

    @PostMapping("/create")
    @ResponseBody
    public CommentDto createComment(@RequestBody CommentDto commentDto, Authentication authentication) {
        return commentService.createComment(commentDto, authentication.getName());
    }

    @PutMapping("/update/{id}")
    @ResponseBody
    public CommentDto updateComment(@PathVariable Long id, @RequestBody CommentDto commentDto) {
        return commentService.updateComment(id, commentDto);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok("Comment deleted successfully");
    }

    @GetMapping("/replies/{commentId}")
    @ResponseBody
    public List<CommentDto> getRepliesByCommentId(@PathVariable Long commentId) {
        return commentService.getRepliesByCommentId(commentId);
    }
}