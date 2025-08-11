package com.instaclone.InstagramClone.service.post;

import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.instaclone.InstagramClone.dto.post.PostResponseDto;
import com.instaclone.InstagramClone.entity.MediaType;


public interface PostService {
	PostResponseDto createPost(MultipartFile file, MediaType mediaType, String title, String description, Set<String> hashtagNames, String username);
    PostResponseDto getPostById(Long postId, String currentUsername);
    Page<PostResponseDto> getPostsByUsernameAndType(String username, MediaType mediaType, int page, int size, String currentUsername);
    Page<PostResponseDto> getFeedForUser(String searchTerm, Pageable pageable, String currentUsername);
    PostResponseDto updatePostCaption(Long postId, String newTitle, String newDescription, String username);
    void deletePost(Long postId, String username);
    Page<PostResponseDto> getUserPosts(String username, MediaType mediaType, String searchTerm, Pageable pageable, String currentUsername);
    //Page<PostResponseDto> getAllPosts(String searchTerm, Pageable pageable, String currentUsername);

    
    byte[] getPostImageContent(Long postId);
    Resource getVideoContent(String videoIdentifier);
}
