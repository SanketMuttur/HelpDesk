package com.helpdesk.repository;

import com.helpdesk.model.Category;
import com.helpdesk.model.Post;
import com.helpdesk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByAuthor(User author);
    List<Post> findByCategory(Category category);
    
    // Enhanced search query to include tags and author username
    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN p.tags t LEFT JOIN p.author a WHERE " +
           "p.title LIKE %?1% OR " +
           "p.content LIKE %?1% OR " +
           "p.category.name LIKE %?1% OR " +
           "t LIKE %?1% OR " +
           "a.username LIKE %?1%")
    List<Post> searchPosts(String keyword);
    
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t = ?1")
    List<Post> findByTag(String tag);
    
    // Add these methods to your PostRepository interface
    List<Post> findByAuthorOrderByCreatedAtDesc(User author);
    long countByAuthor(User author);
    List<Post> findByReportedTrue();

    List<Post> findByHiddenFalse();
    List<Post> findByAuthorAndHiddenFalse(User author);
    List<Post> findByCategoryAndHiddenFalse(Category category);
    
    // @Query("SELECT p FROM Post p WHERE p.hidden = false AND (p.title LIKE %?1% OR p.content LIKE %?1% OR p.category.name LIKE %?1%)")
    // Update this method to include tags in the search criteria
    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN p.tags t LEFT JOIN p.author a WHERE p.hidden = false AND (" +
           "p.title LIKE %?1% OR " +
           "p.content LIKE %?1% OR " +
           "p.category.name LIKE %?1% OR " +
           "t LIKE %?1% OR " +
           "a.username LIKE %?1%)")
    List<Post> searchVisiblePosts(String keyword);
    
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE p.hidden = false AND t = ?1")
    List<Post> findByTagAndHiddenFalse(String tag);
}