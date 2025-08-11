package com.instaclone.InstagramClone.dto.post;

import java.time.LocalDateTime;
import java.util.Set;

import com.instaclone.InstagramClone.dto.user.UserSummaryDto;
import com.instaclone.InstagramClone.entity.MediaType;


public class PostResponseDto {
	private Long id;
    private UserSummaryDto user;
    private MediaType mediaType;
    private String imageUrl;
    private String videoUrl;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long likeCount;
    private int commentCount;
    private boolean likedByCurrentUser;
    private Set<String> hashtags;

    public PostResponseDto() {
    }


    
    public Long getId() {
        return id;
    }

    public UserSummaryDto getUser() {
        return user;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
    	return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }

    public Set<String> getHashtags() {
        return hashtags;
    }

    
    
    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(UserSummaryDto user) {
        this.user = user;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setDescription(String description) {
        this.description= description;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }

    public void setHashtags(Set<String> hashtags) {
        this.hashtags = hashtags;
    }
}
