package com.helpdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private String authorUsername;
    private String authorName;
    private Long postId;
    private Long parentId;
    private List<CommentDto> replies = new ArrayList<>();
    private Date createdAt;
    private Date updatedAt;
    
    // Add these fields to your CommentDto class
    private boolean hidden;
    private String hiddenReason;
    private String hiddenBy;
    private Date hiddenAt;
    private boolean reported;
    private String reportReason;
    private String reportedBy;
    private Date reportedAt;
    private int upvoteCount;
    private int downvoteCount;
    private int voteScore;
    private Boolean userVote; // null if user hasn't voted, true for upvote, false for downvote
}