package com.helpdesk.service;

import com.helpdesk.dto.PostDto;
import com.helpdesk.model.Category;
import com.helpdesk.model.Post;
import com.helpdesk.model.User;
import com.helpdesk.repository.CategoryRepository;
import com.helpdesk.repository.PostRepository;
import com.helpdesk.repository.UserRepository;
import com.helpdesk.repository.VoteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    // private final CategoryService categoryService;
    private final VoteRepository voteRepository;

    @Autowired
    public PostServiceImpl(
            PostRepository postRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            VoteRepository voteRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.voteRepository = voteRepository;
    }

    // Keep the existing getAllPosts method for regular users
    @Override
    public List<PostDto> getAllPosts() {
        return postRepository.findByHiddenFalse().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // Add a new method for admin to see all posts including hidden ones
    @Override
    public List<PostDto> getAllPostsForAdmin() {
        return postRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id: " + id));
        return convertToDto(post);
    }

    @Override
    @Transactional
    public PostDto createPost(PostDto postDto, String username) {
        User author = userRepository.findByUsername(username);
        if (author == null) {
            throw new UsernameNotFoundException("User not found");
        }
        
        Category category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id: " + postDto.getCategoryId()));
        
        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setAuthor(author);
        post.setCategory(category);
        
        // Process tags
        if (postDto.getTags() != null && !postDto.getTags().isEmpty()) {
            post.setTags(postDto.getTags());
        }
        
        Post savedPost = postRepository.save(post);
        return convertToDto(savedPost);
    }

    @Override
    @Transactional
    public PostDto updatePost(Long id, PostDto postDto) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id: " + id));
        
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        
        if (postDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(postDto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category Id: " + postDto.getCategoryId()));
            post.setCategory(category);
        }
        
        // Update tags
        if (postDto.getTags() != null) {
            post.setTags(postDto.getTags());
        }
        
        Post updatedPost = postRepository.save(post);
        return convertToDto(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post Id: " + id));
        
        postRepository.delete(post);
    }

    @Override
    public List<PostDto> getPostsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id: " + categoryId));
        
        return postRepository.findByCategoryAndHiddenFalse(category).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDto> getPostsByTag(String tag) {
        return postRepository.findByTagAndHiddenFalse(tag).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostDto> searchPosts(String keyword) {
        return postRepository.searchVisiblePosts(keyword).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Add these methods to your PostServiceImpl class
    
    @Override
    public long getPostCount() {
        return postRepository.count();
    }
    
    @Override
    public List<PostDto> getPostsByUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        
        List<Post> posts = postRepository.findByAuthorOrderByCreatedAtDesc(user);
        return posts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void togglePostVisibility(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));
        
        post.setHidden(!post.isHidden());
        
        if (post.isHidden()) {
            post.setHiddenAt(new Date());
            post.setHiddenBy("admin"); // In a real app, get the current admin username
        } else {
            post.setHiddenReason(null);
            post.setHiddenBy(null);
            post.setHiddenAt(null);
        }
        
        postRepository.save(post);
    }
    
    @Override
    public List<PostDto> getReportedPosts() {
        List<Post> posts = postRepository.findByReportedTrue();
        return posts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void reportPost(Long id, String reason, String reportedBy) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));
        
        post.setReported(true);
        post.setReportReason(reason);
        post.setReportedBy(reportedBy);
        post.setReportedAt(new Date());
        
        postRepository.save(post);
    }
    
    @Override
    @Transactional
    public void resolveReport(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found with id: " + id));
        
        post.setReported(false);
        post.setReportReason(null);
        post.setReportedBy(null);
        post.setReportedAt(null);
        
        postRepository.save(post);
    }
    
    // Update the convertToDto method to include moderation fields
    private PostDto convertToDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthorUsername(post.getAuthor().getUsername());
        dto.setAuthorName(post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName());
        dto.setCategoryId(post.getCategory().getId());
        dto.setCategoryName(post.getCategory().getName());
        dto.setTags(post.getTags());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setUpvoteCount(voteRepository.countByPostAndUpvote(post, true));
        dto.setDownvoteCount(voteRepository.countByPostAndUpvote(post, false));
        dto.setVoteScore(dto.getUpvoteCount() - dto.getDownvoteCount());
        dto.setHidden(post.isHidden());
        dto.setHiddenReason(post.getHiddenReason());
        dto.setHiddenBy(post.getHiddenBy());
        dto.setHiddenAt(post.getHiddenAt());
        dto.setReported(post.isReported());
        dto.setReportReason(post.getReportReason());
        dto.setReportedBy(post.getReportedBy());
        dto.setReportedAt(post.getReportedAt());
        
        // Add this line to set the comment count
        dto.setCommentCount(post.getComments() != null ? post.getComments().size() : 0);
        
        // We don't load comments here to avoid circular references
        // Comments will be loaded separately when needed
        
        return dto;
    }

    public PostDto getPostWithUserVote(Long id, String username) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));
        
        PostDto dto = convertToDto(post);
        
        if (username != null && !username.isEmpty()) {
            User user = userRepository.findByUsername(username);
            if (user != null) {
                voteRepository.findByUserAndPost(user, post)
                        .ifPresent(vote -> dto.setUserVote(vote.isUpvote()));
            }
        }
        
        return dto;
    }

    @Override
    public List<PostDto> getPostsByUserForAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        return postRepository.findByAuthor(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
}