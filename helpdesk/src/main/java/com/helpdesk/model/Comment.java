package com.helpdesk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 2000)
    private String content;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;
    
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parentComment;
    
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private List<Comment> replies = new ArrayList<>();
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
    
    // Add this field to the Comment class
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votes = new ArrayList<>();
    
    // Add these methods to calculate vote counts
    public int getUpvoteCount() {
        return (int) votes.stream().filter(Vote::isUpvote).count();
    }
    
    public int getDownvoteCount() {
        return (int) votes.stream().filter(vote -> !vote.isUpvote()).count();
    }
    
    public int getVoteScore() {
        return getUpvoteCount() - getDownvoteCount();
    }
    
    // Add these fields to the Comment class
    @Column(name = "is_hidden")
    private boolean hidden = false;
    
    @Column(name = "hidden_reason")
    private String hiddenReason;
    
    @Column(name = "hidden_by")
    private String hiddenBy;
    
    @Column(name = "hidden_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date hiddenAt;
    
    @Column(name = "reported")
    private boolean reported = false;
    
    @Column(name = "report_reason")
    private String reportReason;
    
    @Column(name = "reported_by")
    private String reportedBy;
    
    @Column(name = "reported_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reportedAt;
    
    // Add getters and setters for these fields
}