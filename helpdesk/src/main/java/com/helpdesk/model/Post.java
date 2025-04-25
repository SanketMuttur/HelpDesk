package com.helpdesk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, length = 5000)
    private String content;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;
    
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @ElementCollection
    @CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
    
    // Add this field to the Post class
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
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
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
    
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
    
    // Add these fields to the Post class
    @Column(name = "is_hidden")
    private Boolean hidden = false; // Changed from 'boolean' to 'Boolean' with default value
    
    @Column(name = "hidden_reason")
    private String hiddenReason;
    
    @Column(name = "hidden_by")
    private String hiddenBy;
    
    @Column(name = "hidden_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date hiddenAt;
    
    @Column(name = "reported")
    private Boolean reported = false;
    
    @Column(name = "report_reason")
    private String reportReason;
    
    @Column(name = "reported_by")
    private String reportedBy;
    
    @Column(name = "reported_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reportedAt;
    
    // Update the getter method
    public Boolean getHidden() {
        return hidden;
    }
    
    // Update the setter method
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }
    
    // Add this method to maintain compatibility with existing code
    public boolean isHidden() {
        return hidden != null ? hidden : false;
    }
    
    // Add this method for compatibility
    public boolean isReported() {
        return reported != null ? reported : false;
    }

    // And a setter if needed
    public void setReported(Boolean reported) {
        this.reported = reported;
    }
    
}