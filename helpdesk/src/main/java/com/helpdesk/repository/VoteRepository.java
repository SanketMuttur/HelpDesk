package com.helpdesk.repository;

import com.helpdesk.model.Comment;
import com.helpdesk.model.Post;
import com.helpdesk.model.User;
import com.helpdesk.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByUserAndPost(User user, Post post);
    Optional<Vote> findByUserAndComment(User user, Comment comment);
    
    int countByPostAndUpvote(Post post, boolean upvote);
    int countByCommentAndUpvote(Comment comment, boolean upvote);
}