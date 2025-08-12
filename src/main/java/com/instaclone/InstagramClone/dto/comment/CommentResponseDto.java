package com.instaclone.InstagramClone.dto.comment;

import java.time.LocalDateTime;

import com.instaclone.InstagramClone.dto.user.UserSummaryDto;


public class CommentResponseDto {
	private Long id;
    private String text;
    private UserSummaryDto user;
    private Long postId;
    private LocalDateTime createdAt;

    
    public CommentResponseDto() {
    }

    
    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public UserSummaryDto getUser() {
        return user;
    }

    public Long getPostId() {
        return postId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    
    
    public void setId(Long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setUser(UserSummaryDto user) {
        this.user = user;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
