package com.helpdesk.controller;

import com.helpdesk.dto.VoteDto;
import com.helpdesk.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    private final VoteService voteService;

    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/post/{postId}/upvote")
    public ResponseEntity<VoteDto> upvotePost(@PathVariable Long postId, Authentication authentication) {
        VoteDto voteDto = voteService.votePost(postId, true, authentication.getName());
        return ResponseEntity.ok(voteDto);
    }

    @PostMapping("/post/{postId}/downvote")
    public ResponseEntity<VoteDto> downvotePost(@PathVariable Long postId, Authentication authentication) {
        VoteDto voteDto = voteService.votePost(postId, false, authentication.getName());
        return ResponseEntity.ok(voteDto);
    }

    @PostMapping("/comment/{commentId}/upvote")
    public ResponseEntity<VoteDto> upvoteComment(@PathVariable Long commentId, Authentication authentication) {
        VoteDto voteDto = voteService.voteComment(commentId, true, authentication.getName());
        return ResponseEntity.ok(voteDto);
    }

    @PostMapping("/comment/{commentId}/downvote")
    public ResponseEntity<VoteDto> downvoteComment(@PathVariable Long commentId, Authentication authentication) {
        VoteDto voteDto = voteService.voteComment(commentId, false, authentication.getName());
        return ResponseEntity.ok(voteDto);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<VoteDto> getPostVotes(@PathVariable Long postId, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        VoteDto voteDto = voteService.getPostVotes(postId, username);
        return ResponseEntity.ok(voteDto);
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<VoteDto> getCommentVotes(@PathVariable Long commentId, Authentication authentication) {
        String username = authentication != null ? authentication.getName() : null;
        VoteDto voteDto = voteService.getCommentVotes(commentId, username);
        return ResponseEntity.ok(voteDto);
    }
}