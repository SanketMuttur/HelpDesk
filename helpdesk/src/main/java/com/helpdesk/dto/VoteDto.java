package com.helpdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteDto {
    private Long id;
    private Long postId;
    private Long commentId;
    private boolean upvote;
    private int upvoteCount;
    private int downvoteCount;
    private int voteScore;
    private Boolean userVote; // null if user hasn't voted, true for upvote, false for downvote
}