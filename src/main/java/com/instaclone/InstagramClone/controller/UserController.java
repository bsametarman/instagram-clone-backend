package com.instaclone.InstagramClone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.instaclone.InstagramClone.dto.user.UserProfileDto;
import com.instaclone.InstagramClone.dto.user.UserSummaryDto;
import com.instaclone.InstagramClone.dto.user.UserUpdateRequestDto;
import com.instaclone.InstagramClone.service.user.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
	private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable String username) {
        UserProfileDto userProfile = userService.getUserProfileByUsername(username);
        
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> getCurrentUserProfile(Authentication authentication) {
        String currentUsername = authentication.getName();
        UserProfileDto userProfile = userService.getUserProfileByUsername(currentUsername);
        
        return ResponseEntity.ok(userProfile);
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> updateCurrentUserProfile(Authentication authentication, @Valid @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
        String currentUsername = authentication.getName();
        UserProfileDto updatedProfile = userService.updateUserProfile(currentUsername, userUpdateRequestDto);
        
        return ResponseEntity.ok(updatedProfile);
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, path = "/me/profile-picture")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateProfilePicture(Authentication authentication, @RequestParam("file") MultipartFile file) {
        userService.updateProfilePicture(authentication.getName(), file);
        
        return ResponseEntity.ok().body("Profile picture updated successfully.");
    }

    @GetMapping("/{userId}/profile-picture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable Long userId) {
        byte[] image = userService.getProfilePictureByUserId(userId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        
        return new ResponseEntity<>(image, headers, HttpStatus.OK);
    }
    
    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<UserProfileDto>> getAllUsers(
    		@PageableDefault(page = 0, size = 20, sort = "username", direction = Sort.Direction.ASC)
    		Pageable page) {
        Page<UserProfileDto> userProfiles = userService.getAllUsers(page);
        
        return ResponseEntity.ok(userProfiles);
    }
    
    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN') or isAuthenticated()")
    public ResponseEntity<Page<UserProfileDto>> getAllActiveUsers(
    		@RequestParam(required = false) String searchTerm,
            @PageableDefault(size = 12, sort = "createdDate", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<UserProfileDto> userProfiles = userService.getAllActiveUsers(pageable, searchTerm, true);
        
        return ResponseEntity.ok(userProfiles);
    }
    
    @PostMapping("/{username}/follow")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> followUser(
    		Authentication authentication,
    		@PathVariable String username
    ) {
    	String currentUsername = authentication.getName();
    	UserProfileDto userDto = userService.followUser(currentUsername, username);
    	
    	return ResponseEntity.ok(userDto);
    }
    
    @DeleteMapping("/{username}/follow")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileDto> unfollowUser(
    		Authentication authentication,
    		@PathVariable String username
    ) {
    	String currentUsername = authentication.getName();
    	UserProfileDto userDto = userService.unfollowUser(currentUsername, username);
    	
    	return ResponseEntity.ok(userDto);
    }
    
    @GetMapping("/{username}/followers")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<UserSummaryDto>> getAllFollowers(
    		@PathVariable String username,
    		Pageable page
    ) {
    	Page<UserSummaryDto> userFollowers = userService.findFollowersByUsername(username, page);
    	
    	return ResponseEntity.ok(userFollowers);
    }
    
    @GetMapping("/{username}/following")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<UserSummaryDto>> getAllFollowing(
    		@PathVariable String username,
    		Pageable page
    ) {
    	Page<UserSummaryDto> userFollowings = userService.findFollowingsByUsername(username, page);
    	
    	return ResponseEntity.ok(userFollowings);
    }
    
}
