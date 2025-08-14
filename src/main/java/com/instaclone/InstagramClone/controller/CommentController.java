package com.instaclone.InstagramClone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.instaclone.InstagramClone.dto.comment.CommentResponseDto;
import com.instaclone.InstagramClone.dto.comment.CreateCommentRequestDto;
import com.instaclone.InstagramClone.service.comment.CommentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {
	private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentResponseDto> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequestDto commentRequestDto,
            Authentication authentication) {
        CommentResponseDto createdComment = commentService.addCommentToPost(postId, commentRequestDto, authentication.getName());
        
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<CommentResponseDto>> getCommentsForPost(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CommentResponseDto> comments = commentService.getCommentsByPostId(postId, page, size);
        
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            Authentication authentication) {
        commentService.deleteComment(commentId, authentication.getName());
        
        return ResponseEntity.noContent().build();
    }
}
