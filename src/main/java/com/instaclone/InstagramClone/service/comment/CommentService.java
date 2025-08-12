package com.instaclone.InstagramClone.service.comment;

import java.util.List;

import org.springframework.data.domain.Page;

import com.instaclone.InstagramClone.dto.comment.CommentResponseDto;
import com.instaclone.InstagramClone.dto.comment.CreateCommentRequestDto;
import com.instaclone.InstagramClone.entity.Comment;
import com.instaclone.InstagramClone.entity.Post;

public interface CommentService {
	CommentResponseDto addCommentToPost(Long postId, CreateCommentRequestDto commentRequestDto, String username);
    Page<CommentResponseDto> getCommentsByPostId(Long postId, int page, int size);
    void deleteComment(Long commentId, String username);
	long countByPost(Post post);
    void deleteAll(Iterable<? extends Comment> entities);
    List<Comment> findByPost(Post post);
}
