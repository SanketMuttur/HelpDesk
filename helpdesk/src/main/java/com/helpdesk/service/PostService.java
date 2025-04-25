package com.helpdesk.service;

import com.helpdesk.dto.PostDto;
import com.helpdesk.model.Post;

import java.util.List;

public interface PostService {
    List<PostDto> getAllPosts();
    PostDto getPostById(Long id);
    PostDto createPost(PostDto postDto, String username);
    PostDto updatePost(Long id, PostDto postDto);
    void deletePost(Long id);
    List<PostDto> getPostsByCategory(Long categoryId);
    List<PostDto> getPostsByTag(String tag);
    List<PostDto> searchPosts(String keyword);
    PostDto getPostWithUserVote(Long id, String username);
    
    // Add these methods to the interface
    List<PostDto> getAllPostsForAdmin();
    List<PostDto> getPostsByUserForAdmin(Long userId);
    long getPostCount();
    List<PostDto> getPostsByUser(String username);
    void togglePostVisibility(Long id);
    List<PostDto> getReportedPosts();
    void reportPost(Long id, String reason, String reportedBy);
    void resolveReport(Long id);
}