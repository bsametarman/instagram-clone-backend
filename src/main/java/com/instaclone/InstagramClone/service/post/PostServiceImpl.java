package com.instaclone.InstagramClone.service.post;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.instaclone.InstagramClone.dto.post.PostResponseDto;
import com.instaclone.InstagramClone.dto.user.UserSummaryDto;
import com.instaclone.InstagramClone.entity.Hashtag;
import com.instaclone.InstagramClone.entity.MediaType;
import com.instaclone.InstagramClone.entity.Post;
import com.instaclone.InstagramClone.entity.User;
import com.instaclone.InstagramClone.exception.BadRequestException;
import com.instaclone.InstagramClone.exception.ResourceNotFoundException;
import com.instaclone.InstagramClone.exception.UnauthorizedActionException;
import com.instaclone.InstagramClone.repository.CommentRepository;
import com.instaclone.InstagramClone.repository.LikeRepository;
import com.instaclone.InstagramClone.repository.PostRepository;
import com.instaclone.InstagramClone.repository.UserRepository;
import com.instaclone.InstagramClone.service.fileStorage.FileStorageService;
import com.instaclone.InstagramClone.service.hashtag.HashtagService;
import com.instaclone.InstagramClone.service.user.UserServiceImpl;

@Service
public class PostServiceImpl implements PostService {
	private static final String VIDEO_SUB_DIRECTORY = "videos";

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final HashtagService hashtagService;
    private final FileStorageService fileStorageService;
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public PostServiceImpl(PostRepository postRepository,
                           UserRepository userRepository,
                           LikeRepository likeRepository,
                           CommentRepository commentRepository,
                           HashtagService hashtagService,
                           FileStorageService fileStorageService,
                           UserServiceImpl userServiceImpl) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.hashtagService = hashtagService;
        this.fileStorageService = fileStorageService;
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    @Transactional
    public PostResponseDto createPost(MultipartFile file, MediaType mediaType, String title, String description, Set<String> hashtagNames, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));


        Post post = new Post(user, mediaType, title, description != null ? description : ""); 

        if (mediaType == MediaType.IMAGE) {
            if (file == null || file.isEmpty()) {
                throw new BadRequestException("Image file cannot be empty for an image post.");
            }
            try {
                post.setImageData(file.getBytes());
            } catch (IOException e) {
                throw new BadRequestException("Failed to read image file: " + e.getMessage());
            }
        } else if (mediaType == MediaType.VIDEO) {
            if (file == null || file.isEmpty()) {
                throw new BadRequestException("Video file cannot be empty for a video post.");
            }
            String videoFileName = fileStorageService.storeFile(file, VIDEO_SUB_DIRECTORY);
            post.setVideoFilePath(videoFileName);
        }
        else {
        	throw new BadRequestException("Image field cannot be empty!");
        }

        if (hashtagNames != null && !hashtagNames.isEmpty()) {
            Set<Hashtag> hashtags = hashtagService.findOrCreateHashtags(hashtagNames);
            hashtags.forEach(post::addHashtag);
        }
        
        user.setPostCount(user.getPostCount() + 1);
        userRepository.save(user);
        
        Post savedPost = postRepository.save(post);
        return convertToPostResponseDto(savedPost, username);
    }

    @Override
    public PostResponseDto getPostById(Long postId, String currentUsername) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        return convertToPostResponseDto(post, currentUsername);
    }

    @Override
    public Page<PostResponseDto> getPostsByUsernameAndType(String targetUsername, MediaType mediaType, int page, int size, String currentUsername) {
        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", targetUsername));

        Pageable pageable = PageRequest.of(page, size);

        Page<Post> postPage = postRepository.findByUserAndMediaTypeOrderByCreatedAtDesc(targetUser, mediaType, pageable);

        List<PostResponseDto> dtos = postPage.getContent().stream()
                .map(post -> convertToPostResponseDto(post, currentUsername))
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, postPage.getTotalElements());
    }
    
    @Override
    public Page<PostResponseDto> getFeedForUser(String searchTerm, Pageable pageable, String currentUsername) {
        Page<Post> postPage;

        if (searchTerm != null && !searchTerm.isBlank()) {
            postPage = postRepository.findAllBySearchTerm(searchTerm, pageable);
        } else {
            postPage = postRepository.findAll(pageable);
        }
        
        return postPage.map(post -> convertToPostResponseDto(post, currentUsername));
    }


    @Override
    @Transactional
    public PostResponseDto updatePostCaption(Long postId, String newTitle, String newDescription, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You are not authorized to update this post.");
        }

        post.setTitle(newTitle);
        post.setDescription(newDescription);
        Post updatedPost = postRepository.save(post);
        return convertToPostResponseDto(updatedPost, username);
    }

    @Override
    @Transactional
    public void deletePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedActionException("You are not authorized to delete this post.");
        }

        likeRepository.deleteAll(likeRepository.findByPost(post));
        commentRepository.deleteAll(commentRepository.findByPostOrderByCreatedAtAsc(post));

        if (post.getMediaType() == MediaType.VIDEO && post.getVideoFilePath() != null) {
            fileStorageService.deleteFile(post.getVideoFilePath(), VIDEO_SUB_DIRECTORY);
        }
        
        User postOwner = post.getUser();
        postOwner.setPostCount(Math.max(0, postOwner.getPostCount() - 1));
        userRepository.save(postOwner);
        
        postRepository.delete(post);
    }
    
    @Override
    public Page<PostResponseDto> getUserPosts(String targetUsername, MediaType mediaType, String searchTerm, Pageable pageable, String currentUsername) {
        User targetUser = userRepository.findByUsername(targetUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", targetUsername));

        Page<Post> postPage = postRepository.findByUserAndMediaTypeAndSearchTerm(
            targetUser, 
            mediaType, 
            searchTerm, 
            pageable
        );

        return postPage.map(post -> convertToPostResponseDto(post, currentUsername));
    }
    

    @Override
    @Transactional(readOnly = true)
    public byte[] getPostImageContent(Long postId) {
    	return postRepository.findImageDataById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Image content not found for post", "id", postId));
    }

    @Override
    public Resource getVideoContent(String videoIdentifier) {
        if (videoIdentifier == null || videoIdentifier.isBlank()) {
            throw new BadRequestException("Video identifier cannot be empty.");
        }
        return fileStorageService.loadFileAsResource(videoIdentifier, VIDEO_SUB_DIRECTORY);
    }
    
    @Override
	public Page<PostResponseDto> getMainPageFeed(String currentUsername, Pageable page) {
		User currentUser = userRepository.findByUsername(currentUsername).orElseThrow(() -> new ResourceNotFoundException("User could not found!"));
		
		List<Long> followingsUserIds = currentUser.getFollowing().stream().map(User::getId).collect(Collectors.toList());
		
		Page<Post> followingsPosts = postRepository.findByUser_IdInOrderByCreatedAtDesc(followingsUserIds, page);
		
		return followingsPosts.map(post -> convertToPostResponseDto(post, currentUsername));
	}


    private PostResponseDto convertToPostResponseDto(Post post, String currentUsername) {
        PostResponseDto dto = new PostResponseDto();
        dto.setId(post.getId());

        UserSummaryDto userSummaryDto = userServiceImpl.convertToUserSummaryDto(post.getUser());
        dto.setUser(userSummaryDto);

        dto.setMediaType(post.getMediaType());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setLikeCount(post.getLikes() != null ? post.getLikes().size() : 0);
        dto.setCommentCount(post.getComments() != null ? post.getComments().size() : 0);
        
        if (post.getMediaType() == MediaType.IMAGE && post.getImageData() != null) {
            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/media/posts/image/")
                    .path(String.valueOf(post.getId()))
                    .toUriString();
            dto.setImageUrl(imageUrl);
        } else if (post.getMediaType() == MediaType.VIDEO && post.getVideoFilePath() != null) {
            String videoUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/media/posts/video/")
                    .path(post.getVideoFilePath())
                    .toUriString();
            dto.setVideoUrl(videoUrl);
        }


        if (currentUsername != null && !currentUsername.isEmpty()) {
            User currentUser = userRepository.findByUsername(currentUsername).orElse(null);
            if (currentUser != null) {
                boolean liked = post.getLikes() != null && post.getLikes().stream()
                                .anyMatch(like -> like.getUser().getId().equals(currentUser.getId()));
                dto.setLikedByCurrentUser(liked);
            } else {
                dto.setLikedByCurrentUser(false);
            }
        } else {
            dto.setLikedByCurrentUser(false);
        }


        if (post.getHashtags() != null) {
            dto.setHashtags(post.getHashtags().stream().map(Hashtag::getName).collect(Collectors.toSet()));
        } else {
            dto.setHashtags(Collections.emptySet());
        }

        if (post.getMediaType() == MediaType.IMAGE && post.getImageData() != null) {
            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/media/posts/image/")
                    .path(String.valueOf(post.getId()))
                    .toUriString();
            dto.setImageUrl(imageUrl);
        } else if (post.getMediaType() == MediaType.VIDEO && post.getVideoFilePath() != null) {
            String videoUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/media/posts/video/")
                    .path(post.getVideoFilePath())
                    .toUriString();
            dto.setVideoUrl(videoUrl);
        }
        return dto;
    }

}
