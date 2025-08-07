package com.instaclone.InstagramClone.dto.user;

import java.time.LocalDateTime;

public class UserProfileDto {
	private Long id;
    private String username;
    private String firstNname;
    private String lastName;
    private String bio;
    private String profilePictureUrl;
    private LocalDateTime createdAt;
    private boolean isActive;
    private int postCount;

    // Constructors
    public UserProfileDto() {
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstNname;
    }
    
    public String getLastName() {
    	return lastName;
    }

    public String getBio() {
        return bio;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public LocalDateTime getCreatedAt() {
    	return createdAt;
    }
    
    public boolean isActive() {
    	return isActive;
    }

    public int getPostCount() {
        return postCount;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String firstNname) {
        this.firstNname = firstNname;
    }

    public void setLastName(String lastName) {
    	this.lastName = lastName;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }
}
