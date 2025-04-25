package com.helpdesk.service;

import com.helpdesk.dto.CommentDto;
import java.util.List;

public interface CommentService {
    // Uncomment this method if it's still being used in your implementation
    List<CommentDto> getCommentsByPostId(Long postId);
    
    // Add this method to your interface
    List<CommentDto> getCommentsByPost(Long postId, String username);
    
    CommentDto getCommentById(Long id);
    CommentDto createComment(CommentDto commentDto, String username);
    CommentDto updateComment(Long id, CommentDto commentDto);
    void deleteComment(Long id);
    List<CommentDto> getRepliesByCommentId(Long commentId);
    List<CommentDto> getCommentsWithUserVote(Long postId, String username);
    long getCommentCount();
    List<CommentDto> getAllComments();
    List<CommentDto> getCommentsByUser(String username);
    void toggleCommentVisibility(Long id);
    List<CommentDto> getReportedComments();
    void reportComment(Long id, String reason, String reportedBy);
    void resolveReport(Long id);
    List<CommentDto> getCommentsByUserForAdmin(Long userId);
}