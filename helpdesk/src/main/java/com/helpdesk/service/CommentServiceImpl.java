package com.helpdesk.service;

import com.helpdesk.dto.CommentDto;
import com.helpdesk.model.Comment;
import com.helpdesk.model.Post;
import com.helpdesk.model.User;
import com.helpdesk.repository.CommentRepository;
import com.helpdesk.repository.PostRepository;
import com.helpdesk.repository.UserRepository;
import com.helpdesk.repository.VoteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    @Autowired
    public CommentServiceImpl(
            CommentRepository commentRepository,
            PostRepository postRepository,
            UserRepository userRepository,
            VoteRepository voteRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
    }

    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findTopLevelCommentsByPostId(postId);
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + id));
        return convertToDto(comment);
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        Post post = postRepository.findById(commentDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setAuthor(user);
        comment.setPost(post);

        if (commentDto.getParentId() != null) {
            Comment parentComment = commentRepository.findById(commentDto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
            comment.setParentComment(parentComment);
        }

        Comment savedComment = commentRepository.save(comment);
        return convertToDto(savedComment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long id, CommentDto commentDto) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        comment.setContent(commentDto.getContent());
        Comment updatedComment = commentRepository.save(comment);
        return convertToDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDto> getRepliesByCommentId(Long commentId) {
        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        
        List<Comment> replies = commentRepository.findByParentCommentOrderByCreatedAtAsc(parentComment);
        return replies.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Add these methods to your CommentServiceImpl class

    @Override
    public long getCommentCount() {
        return commentRepository.count();
    }

    @Override
    public List<CommentDto> getAllComments() {
        List<Comment> comments = commentRepository.findAll();
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getCommentsByUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        
        List<Comment> comments = commentRepository.findByAuthorOrderByCreatedAtDesc(user);
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void toggleCommentVisibility(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + id));
        
        comment.setHidden(!comment.isHidden());
        
        if (comment.isHidden()) {
            comment.setHiddenAt(new Date());
            comment.setHiddenBy("admin"); // In a real app, get the current admin username
        } else {
            comment.setHiddenReason(null);
            comment.setHiddenBy(null);
            comment.setHiddenAt(null);
        }
        
        commentRepository.save(comment);
    }

    @Override
    public List<CommentDto> getReportedComments() {
        List<Comment> comments = commentRepository.findByReportedTrue();
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void reportComment(Long id, String reason, String reportedBy) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + id));
        
        comment.setReported(true);
        comment.setReportReason(reason);
        comment.setReportedBy(reportedBy);
        comment.setReportedAt(new Date());
        
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void resolveReport(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found with id: " + id));
        
        comment.setReported(false);
        comment.setReportReason(null);
        comment.setReportedBy(null);
        comment.setReportedAt(null);
        
        commentRepository.save(comment);
    }

    // Update the convertToDto method to include moderation fields
    private CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setAuthorUsername(comment.getAuthor().getUsername());
        dto.setAuthorName(comment.getAuthor().getFirstName() + " " + comment.getAuthor().getLastName());
        dto.setPostId(comment.getPost().getId());
        
        if (comment.getParentComment() != null) {
            dto.setParentId(comment.getParentComment().getId());
        }
        
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        
        // Recursively add replies
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            List<CommentDto> replyDtos = comment.getReplies().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            dto.setReplies(replyDtos);
        }

        dto.setUpvoteCount(voteRepository.countByCommentAndUpvote(comment, true));
        dto.setDownvoteCount(voteRepository.countByCommentAndUpvote(comment, false));
        dto.setVoteScore(dto.getUpvoteCount() - dto.getDownvoteCount());
        
        // Add moderation fields
        dto.setHidden(comment.isHidden());
        dto.setHiddenReason(comment.getHiddenReason());
        dto.setHiddenBy(comment.getHiddenBy());
        dto.setHiddenAt(comment.getHiddenAt());
        dto.setReported(comment.isReported());
        dto.setReportReason(comment.getReportReason());
        dto.setReportedBy(comment.getReportedBy());
        dto.setReportedAt(comment.getReportedAt());
        
        return dto;
    }

    public List<CommentDto> getCommentsWithUserVote(Long postId, String username) {
        List<Comment> comments = commentRepository.findByPostIdAndParentCommentIsNullAndHiddenFalseOrderByCreatedAtDesc(postId);
        List<CommentDto> dtos = comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        if (username != null && !username.isEmpty()) {
            User user = userRepository.findByUsername(username);
            if (user != null) {
                for (CommentDto dto : dtos) {
                    Comment comment = commentRepository.findById(dto.getId()).orElse(null);
                    if (comment != null) {
                        voteRepository.findByUserAndComment(user, comment)
                                .ifPresent(vote -> dto.setUserVote(vote.isUpvote()));
                    }
                    
                    // Also set user votes for replies
                    if (dto.getReplies() != null && !dto.getReplies().isEmpty()) {
                        for (CommentDto replyDto : dto.getReplies()) {
                            Comment reply = commentRepository.findById(replyDto.getId()).orElse(null);
                            if (reply != null) {
                                voteRepository.findByUserAndComment(user, reply)
                                        .ifPresent(vote -> replyDto.setUserVote(vote.isUpvote()));
                            }
                        }
                    }
                }
            }
        }
        
        return dtos;
    }

    @Override
    public List<CommentDto> getCommentsByUserForAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        List<Comment> comments = commentRepository.findByAuthorOrderByCreatedAtDesc(user);
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getCommentsByPost(Long postId, String username) {
        // Get all top-level comments (no parent) for the post that aren't hidden
        List<Comment> topLevelComments = commentRepository.findByPostIdAndParentCommentIsNullAndHiddenFalseOrderByCreatedAtDesc(postId);
        
        List<CommentDto> commentDtos = new ArrayList<>();
        
        for (Comment comment : topLevelComments) {
            CommentDto dto = convertToDto(comment);
            
            // Recursively get all replies for this comment (that aren't hidden)
            dto.setReplies(getRepliesRecursiveWithUserVote(comment.getId(), username));
            commentDtos.add(dto);
        }
        
        return commentDtos;
    }

    // Helper method to get replies with user votes
    private List<CommentDto> getRepliesRecursiveWithUserVote(Long parentId, String username) {
        // Get all direct replies to this comment that aren't hidden
        List<Comment> replies = commentRepository.findByParentCommentIdAndHiddenFalseOrderByCreatedAtAsc(parentId);
        
        List<CommentDto> replyDtos = new ArrayList<>();
        
        for (Comment reply : replies) {
            CommentDto dto = convertToDto(reply);
            
            // Recursively get all replies to this reply
            dto.setReplies(getRepliesRecursiveWithUserVote(reply.getId(), username));
            replyDtos.add(dto);
        }
        
        return replyDtos;
    }
}