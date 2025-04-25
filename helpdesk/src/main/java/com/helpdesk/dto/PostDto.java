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
public class PostDto {
    private Long id;
    private String title;
    private String content;
    private String authorUsername;
    private String authorName;
    private Long categoryId;
    private String categoryName;
    private List<String> tags = new ArrayList<>();
    private List<CommentDto> comments = new ArrayList<>();
    private Date createdAt;
    private Date updatedAt;
    
    // Add these fields to the PostDto class
    private int upvoteCount;
    private int downvoteCount;
    private int voteScore;
    private Boolean userVote; // null if user hasn't voted, true for upvote, false for downvote
    
    // Add these fields to your PostDto class
    private boolean hidden;
    private String hiddenReason;
    private String hiddenBy;
    private Date hiddenAt;
    private boolean reported;
    private String reportReason;
    private String reportedBy;
    private Date reportedAt;
    
    // Add this field to your PostDto class
    private Integer commentCount;
    
    // Add getter and setter
    public Integer getCommentCount() {
        return commentCount;
    }
    
    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
}