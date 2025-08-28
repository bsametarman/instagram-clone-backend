package com.instaclone.InstagramClone.service.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.instaclone.InstagramClone.dto.user.JwtResponseDto;
import com.instaclone.InstagramClone.dto.user.SignUpRequestDto;
import com.instaclone.InstagramClone.dto.user.UserProfileDto;
import com.instaclone.InstagramClone.dto.user.UserSummaryDto;
import com.instaclone.InstagramClone.dto.user.UserUpdateRequestDto;

public interface UserService {
	JwtResponseDto registerUser(SignUpRequestDto signUpRequestDto);
    UserProfileDto getUserProfileByUsername(String username);
    UserProfileDto updateUserProfile(String currentUsername, UserUpdateRequestDto userUpdateRequestDto);
    void updateProfilePicture(String currentUsername, MultipartFile file);
    byte[] getProfilePictureByUserId(Long userId);
    Page<UserProfileDto> getAllUsers(Pageable page);
    Page<UserProfileDto> getAllActiveUsers(Pageable page, String searchTerm, boolean isActive);
    UserProfileDto followUser(String followerUsername, String followingUsername);
    UserProfileDto unfollowUser(String followerUsername, String followingUsername);
    Page<UserSummaryDto> findFollowersByUsername(String currentUsername, Pageable pageable);
    Page<UserSummaryDto> findFollowingsByUsername(String currentUSername, Pageable pageable);
    Boolean isFollowedByUser(String currentUsername, String username);
}
