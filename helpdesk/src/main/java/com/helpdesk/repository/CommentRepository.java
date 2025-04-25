package com.helpdesk.repository;

import com.helpdesk.model.Comment;
import com.helpdesk.model.Post;
import com.helpdesk.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostOrderByCreatedAtAsc(Post post);
    
    @Query("SELECT c FROM Comment c WHERE c.post.id = ?1 AND c.parentComment IS NULL ORDER BY c.createdAt ASC")
    List<Comment> findTopLevelCommentsByPostId(Long postId);
    
    List<Comment> findByParentCommentOrderByCreatedAtAsc(Comment parentComment);
    
    // Add these methods to your CommentRepository interface
    List<Comment> findByAuthorOrderByCreatedAtDesc(User author);
    List<Comment> findByAuthor(User author);
    long countByAuthor(User author);
    List<Comment> findByReportedTrue();
    
    // Add these methods to filter out hidden comments
    List<Comment> findByPostIdAndHiddenFalseOrderByCreatedAtAsc(Long postId);
    
    // With these methods that match your entity structure
    List<Comment> findByPostIdAndParentCommentIsNullAndHiddenFalseOrderByCreatedAtDesc(Long postId);
    List<Comment> findByParentCommentIdAndHiddenFalseOrderByCreatedAtAsc(Long parentCommentId);
}