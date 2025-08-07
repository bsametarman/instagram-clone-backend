package com.instaclone.InstagramClone.dto.user;

public class UserSummaryDto {
	private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;

    public UserSummaryDto() {
    }

    public UserSummaryDto(Long id, String username, String firstName, String lastName, String profilePictureUrl) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.profilePictureUrl = profilePictureUrl;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
    	return firstName;
    }
    
    public String getLastName() {
    	return lastName;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
}
