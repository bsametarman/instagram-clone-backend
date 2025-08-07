package com.instaclone.InstagramClone.dto.user;

import jakarta.validation.constraints.Size;

public class UserUpdateRequestDto {
	@Size(max = 50, message = "Name cannot be longer than 50 characters")
    private String firstName;
	
	@Size(max = 50, message = "Last name cannot be longer than 50 characters")
    private String lastName;

    @Size(max = 2000, message = "Bio cannot be longer than 150 characters")
    private String bio;
    
    private boolean isActive = true;

    // Getters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
    	return lastName;
    }

    public String getBio() {
        return bio;
    }
    
    public boolean getActive() {
    	return this.isActive;
    }

    // Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
    	this.lastName = lastName;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
