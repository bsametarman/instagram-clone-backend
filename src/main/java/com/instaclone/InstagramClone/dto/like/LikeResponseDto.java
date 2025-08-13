package com.instaclone.InstagramClone.dto.like;

public class LikeResponseDto {
	private boolean success;
    private boolean liked;
    private int newLikeCount;

    public LikeResponseDto(boolean success, boolean liked, int newLikeCount) {
        this.success = success;
        this.liked = liked;
        this.newLikeCount = newLikeCount;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isLiked() {
        return liked;
    }

    public int getNewLikeCount() {
        return newLikeCount;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setNewLikeCount(int newLikeCount) {
        this.newLikeCount = newLikeCount;
    }
}
