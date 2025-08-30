package com.instaclone.InstagramClone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.instaclone.InstagramClone.dto.post.CreatePostRequestDto;
import com.instaclone.InstagramClone.dto.post.PagedResponseDto;
import com.instaclone.InstagramClone.dto.post.PostResponseDto;
import com.instaclone.InstagramClone.service.post.PostService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts")
public class PostController {
	private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostResponseDto> createPost(
            @RequestPart("file") MultipartFile file,
            @Valid @RequestPart("postData") CreatePostRequestDto postData,
            Authentication authentication) {

        PostResponseDto createdPost = postService.createPost(
        		file,
                postData.getMediaType(),
                postData.getTitle(),
                postData.getDescription(),
                postData.getHashtags(),
                authentication.getName()
        );
        
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long postId, Authentication authentication) {
        String currentUsername = (authentication != null) ? authentication.getName() : null;
        PostResponseDto post = postService.getPostById(postId, currentUsername);
        
        return ResponseEntity.ok(post);
    }
    
    @GetMapping("/user/{username}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResponseDto<PostResponseDto>> getPostsByUser(
            @PathVariable String username,
            @RequestParam com.instaclone.InstagramClone.entity.MediaType mediaType,
            @RequestParam(required = false) String searchTerm,
            @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {
        
        String currentUsername = (authentication != null) ? authentication.getName() : null;
        
        Page<PostResponseDto> postsPage = postService.getUserPosts(username, mediaType, searchTerm, pageable, currentUsername);
        
        PagedResponseDto<PostResponseDto> responseDto = new PagedResponseDto<>(postsPage);
        
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/feed")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResponseDto<PostResponseDto>> getFeed(
    		@RequestParam(required = false) String searchTerm,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            Authentication authentication) {
        String currentUsername = (authentication != null) ? authentication.getName() : null;
        
        Page<PostResponseDto> feed = postService.getFeedForUser(searchTerm, pageable, currentUsername);
        
        PagedResponseDto<PostResponseDto> responseDto = new PagedResponseDto<>(feed);
        
        return ResponseEntity.ok(responseDto);
    }
    
    @GetMapping("/main")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PagedResponseDto<PostResponseDto>> getMainPageFeed(
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable,
            Authentication authentication) {
        String currentUsername = (authentication != null) ? authentication.getName() : null;
        
        Page<PostResponseDto> mainPageFeed = postService.getMainPageFeed(currentUsername, pageable);
        
        PagedResponseDto<PostResponseDto> responseDto = new PagedResponseDto<>(mainPageFeed);
        
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, Authentication authentication) {
        postService.deletePost(postId, authentication.getName());
        
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
