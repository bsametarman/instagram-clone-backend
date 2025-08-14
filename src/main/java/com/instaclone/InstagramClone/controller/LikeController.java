package com.instaclone.InstagramClone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.instaclone.InstagramClone.dto.like.LikeResponseDto;
import com.instaclone.InstagramClone.service.like.LikeService;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
public class LikeController {
	private final LikeService likeService;

    @Autowired
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<LikeResponseDto> toggleLike(
            @PathVariable Long postId,
            Authentication authentication) {
        LikeResponseDto response = likeService.toggleLikePost(postId, authentication.getName());
        
        return ResponseEntity.ok(response);
    }
}
