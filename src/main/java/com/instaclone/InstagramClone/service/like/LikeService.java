package com.instaclone.InstagramClone.service.like;

import com.instaclone.InstagramClone.dto.like.LikeResponseDto;

public interface LikeService {
	LikeResponseDto toggleLikePost(Long postId, String username);
}
