package com.instaclone.InstagramClone.dto.post;

import java.util.Set;

import com.instaclone.InstagramClone.entity.MediaType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreatePostRequestDto {
	@NotNull(message = "Media type cannot be null")
    private MediaType mediaType;

	@Size(max = 200, message = "Title cannot be longer than 200 characters")
    private String title;
	
    @Size(max = 2200, message = "Description cannot be longer than 2200 characters")
    private String description;

    private Set<String> hashtags;


    // Getters
    public MediaType getMediaType() {
        return mediaType;
    }
   
    public String getTitle() {
    	return title;
    }

    public String getDescription() {
        return description;
    }

    public Set<String> getHashtags() {
        return hashtags;
    }

    // Setters
    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public void setTitle(String title) {
    	this.title = title;
    }
    
    public void setCaption(String description) {
        this.description = description;
    }

    public void setHashtags(Set<String> hashtags) {
        this.hashtags = hashtags;
    }

}
