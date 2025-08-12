package com.instaclone.InstagramClone.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCommentRequestDto {
	@NotBlank(message = "Comment text cannot be blank")
    @Size(max = 1000, message = "Comment cannot be longer than 1000 characters")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
