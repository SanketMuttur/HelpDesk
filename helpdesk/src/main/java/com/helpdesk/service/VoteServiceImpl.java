package com.helpdesk.service;

import com.helpdesk.dto.VoteDto;
import com.helpdesk.model.Comment;
import com.helpdesk.model.Post;
import com.helpdesk.model.User;
import com.helpdesk.model.Vote;
import com.helpdesk.repository.CommentRepository;
import com.helpdesk.repository.PostRepository;
import com.helpdesk.repository.UserRepository;
import com.helpdesk.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Autowired
    public VoteServiceImpl(
            VoteRepository voteRepository,
            PostRepository postRepository,
            CommentRepository commentRepository,
            UserRepository userRepository) {
        this.voteRepository = voteRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public VoteDto votePost(Long postId, boolean upvote, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Optional<Vote> existingVote = voteRepository.findByUserAndPost(user, post);

        Vote vote;
        if (existingVote.isPresent()) {
            vote = existingVote.get();
            // If the vote is the same, remove it (toggle)
            if (vote.isUpvote() == upvote) {
                voteRepository.delete(vote);
                return getPostVotes(postId, username);
            }
            // Otherwise, update the vote
            vote.setUpvote(upvote);
        } else {
            vote = new Vote();
            vote.setUser(user);
            vote.setPost(post);
            vote.setUpvote(upvote);
        }

        voteRepository.save(vote);
        return getPostVotes(postId, username);
    }

    @Override
    @Transactional
    public VoteDto voteComment(Long commentId, boolean upvote, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        Optional<Vote> existingVote = voteRepository.findByUserAndComment(user, comment);

        Vote vote;
        if (existingVote.isPresent()) {
            vote = existingVote.get();
            // If the vote is the same, remove it (toggle)
            if (vote.isUpvote() == upvote) {
                voteRepository.delete(vote);
                return getCommentVotes(commentId, username);
            }
            // Otherwise, update the vote
            vote.setUpvote(upvote);
        } else {
            vote = new Vote();
            vote.setUser(user);
            vote.setComment(comment);
            vote.setUpvote(upvote);
        }

        voteRepository.save(vote);
        return getCommentVotes(commentId, username);
    }

    @Override
    public VoteDto getPostVotes(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        VoteDto voteDto = new VoteDto();
        voteDto.setPostId(postId);
        voteDto.setUpvoteCount(voteRepository.countByPostAndUpvote(post, true));
        voteDto.setDownvoteCount(voteRepository.countByPostAndUpvote(post, false));
        voteDto.setVoteScore(voteDto.getUpvoteCount() - voteDto.getDownvoteCount());

        // Get the user's vote if they're logged in
        if (username != null && !username.isEmpty()) {
            User user = userRepository.findByUsername(username);
            if (user != null) {
                Optional<Vote> userVote = voteRepository.findByUserAndPost(user, post);
                userVote.ifPresent(vote -> voteDto.setUserVote(vote.isUpvote()));
            }
        }

        return voteDto;
    }

    @Override
    public VoteDto getCommentVotes(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        VoteDto voteDto = new VoteDto();
        voteDto.setCommentId(commentId);
        voteDto.setUpvoteCount(voteRepository.countByCommentAndUpvote(comment, true));
        voteDto.setDownvoteCount(voteRepository.countByCommentAndUpvote(comment, false));
        voteDto.setVoteScore(voteDto.getUpvoteCount() - voteDto.getDownvoteCount());

        // Get the user's vote if they're logged in
        if (username != null && !username.isEmpty()) {
            User user = userRepository.findByUsername(username);
            if (user != null) {
                Optional<Vote> userVote = voteRepository.findByUserAndComment(user, comment);
                userVote.ifPresent(vote -> voteDto.setUserVote(vote.isUpvote()));
            }
        }

        return voteDto;
    }
}