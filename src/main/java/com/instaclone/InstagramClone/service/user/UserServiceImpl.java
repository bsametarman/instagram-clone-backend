package com.instaclone.InstagramClone.service.user;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.instaclone.InstagramClone.dto.user.JwtResponseDto;
import com.instaclone.InstagramClone.dto.user.SignUpRequestDto;
import com.instaclone.InstagramClone.dto.user.UserProfileDto;
import com.instaclone.InstagramClone.dto.user.UserSummaryDto;
import com.instaclone.InstagramClone.dto.user.UserUpdateRequestDto;
import com.instaclone.InstagramClone.entity.ERole;
import com.instaclone.InstagramClone.entity.Role;
import com.instaclone.InstagramClone.entity.User;
import com.instaclone.InstagramClone.exception.BadRequestException;
import com.instaclone.InstagramClone.exception.ResourceNotFoundException;
import com.instaclone.InstagramClone.repository.RoleRepository;
import com.instaclone.InstagramClone.repository.UserRepository;
import com.instaclone.InstagramClone.security.UserPrincipal;
import com.instaclone.InstagramClone.security.jwt.JwtTokenProvider;

@Service
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
    						RoleRepository roleRepository,
    						PasswordEncoder passwordEncoder,
    						AuthenticationManager authenticationManager,
    						JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public JwtResponseDto registerUser(SignUpRequestDto signUpRequestDto) {
        if (userRepository.existsByUsername(signUpRequestDto.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }
        if (userRepository.existsByEmail(signUpRequestDto.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }

        User user = new User();
        user.setUsername(signUpRequestDto.getUsername());
        user.setEmail(signUpRequestDto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(signUpRequestDto.getPassword()));
        user.setFirstName(signUpRequestDto.getFirstName());
        user.setLastName(signUpRequestDto.getLastName());
        user.setCreatedDate(LocalDateTime.now());
        user.setUpdatedDate(LocalDateTime.now());
        user.setBio(null);
        user.setProfilePicture(null);

        User savedUser = userRepository.save(user);
        
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        
		savedUser.getRoles().add(userRole);
		userRepository.save(savedUser);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                		signUpRequestDto.getUsername(),
                		signUpRequestDto.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        return new JwtResponseDto(jwt, userPrincipal.getId(), userPrincipal.getUsername(), userPrincipal.getEmail());
        
        //return new JwtResponseDto("DUMMY_TOKEN_FOR_TESTING", savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
    }

    @Override
    public UserProfileDto getUserProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return convertToUserProfileDto(user);
    }

    @Override
    @Transactional
    public UserProfileDto updateUserProfile(String currentUsername, UserUpdateRequestDto userUpdateRequestDto) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));

        if (userUpdateRequestDto.getFirstName() != null) {
            user.setFirstName(userUpdateRequestDto.getFirstName());
        }
        if (userUpdateRequestDto.getLastName() != null) {
        	user.setLastName(userUpdateRequestDto.getLastName());
        }
        if (userUpdateRequestDto.getBio() != null) {
            user.setBio(userUpdateRequestDto.getBio());
        }
        if (userUpdateRequestDto.getActive() != user.isActive()) {
            user.setActive(userUpdateRequestDto.getActive());
        }

        User updatedUser = userRepository.save(user);
        return convertToUserProfileDto(updatedUser);
    }

    @Override
    @Transactional
    public void updateProfilePicture(String currentUsername, MultipartFile file) {
        User user = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername));

        if (file.isEmpty()) {
            throw new BadRequestException("Cannot upload an empty file.");
        }
        try {
            user.setProfilePicture(file.getBytes());
            userRepository.save(user);
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload profile picture: " + e.getMessage());
        }
    }

    @Override
    public byte[] getProfilePictureByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (user.getProfilePicture() == null) {
            throw new ResourceNotFoundException("Profile picture", "userId", userId);
        }
        return user.getProfilePicture();
    }
    
    @Override
	public Page<UserProfileDto> getAllUsers(Pageable page) {
    	Page<UserProfileDto> usersDto;
		
		Page<User> users = userRepository.findAll(page);
		
		if(!users.isEmpty()) {
			usersDto = users.map(this::convertToUserProfileDto);
		}
		else {
			throw new ResourceNotFoundException("Users");
		}
		
		return usersDto;
	}
    
    @Override
    @Transactional(readOnly = true)
	public Page<UserProfileDto> getAllActiveUsers(Pageable page, String searchTerm, boolean isActive) {
    	Page<UserProfileDto> usersDto;
    	Page<User> users;
		
    	if (searchTerm != null && !searchTerm.isBlank()) {
    		users = userRepository.findBySearchTermAndIsActive(searchTerm, page, isActive);
    	} else {
            users = userRepository.findAllUserByIsActive(page, isActive);
        }
		
		if(!users.isEmpty()) {
			usersDto = users.map(this::convertToUserProfileDto);
		}
		else {
			throw new ResourceNotFoundException("Users");
		}
		
		return usersDto;
	}

    private UserProfileDto convertToUserProfileDto(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setBio(user.getBio());
        dto.setCreatedAt(user.getCreatedDate());
        dto.setPostCount(user.getPostCount());
        dto.setFollowerCount(user.getFollowerCount());
        dto.setFollowingCount(user.getFollowingCount());

        if (user.getProfilePicture() != null && user.getProfilePicture().length > 0) {
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/users/")
                    .path(user.getId().toString())
                    .path("/profile-picture")
                    .toUriString();
            dto.setProfilePictureUrl(fileDownloadUri);
        } else {
        }
        return dto;
    }

    public UserSummaryDto convertToUserSummaryDto(User user) {
        UserSummaryDto summaryDto = new UserSummaryDto();
        summaryDto.setId(user.getId());
        summaryDto.setUsername(user.getUsername());
        if (user.getProfilePicture() != null && user.getProfilePicture().length > 0) {
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/users/")
                    .path(user.getId().toString())
                    .path("/profile-picture")
                    .toUriString();
            summaryDto.setProfilePictureUrl(fileDownloadUri);
        }
        return summaryDto;
    }
    
    @Override
    @Transactional
    public UserProfileDto followUser(String followerUsername, String followedUsername) {
    	User followingUser = userRepository.findByUsername(followerUsername).orElseThrow(
    			() -> new ResourceNotFoundException("User wanted to follow could not found!")
    	);
    	User followedUser = userRepository.findByUsername(followedUsername).orElseThrow(
    			() -> new ResourceNotFoundException("User to follow could not found!")
    	);
    	
    	if(followingUser.getUsername().equals(followedUser.getUsername())) {
    		throw new RuntimeException("You cannot follow yourself!");
    	}
    	
    	if(followedUser.getFollowers().contains(followingUser)) {
    		throw new RuntimeException("You have already followed this user!");
    	}
    	
    	// User that follow others
    	followingUser.getFollowing().add(followedUser);
    	followingUser.setFollowingCount(followingUser.getFollowingCount() + 1);
    	
    	// User that followed by others
    	followedUser.getFollowers().add(followingUser);
    	followedUser.setFollowerCount(followedUser.getFollowerCount() + 1);
    	
    	userRepository.save(followingUser);
    	userRepository.save(followedUser);
    	
    	return convertToUserProfileDto(followingUser);
    }
    
    @Override
    @Transactional
    public UserProfileDto unfollowUser(String followerUsername, String followedUsername) {
    	User followingUser = userRepository.findByUsername(followerUsername).orElseThrow(
    			() -> new ResourceNotFoundException("User wanted to follow could not found!")
    	);
    	User followedUser = userRepository.findByUsername(followedUsername).orElseThrow(
    			() -> new ResourceNotFoundException("User to follow could not found!")
    	);
    	
    	if(followingUser.getUsername().equals(followedUser.getUsername())) {
    		throw new RuntimeException("You cannot unfollow yourself!");
    	}
    	
    	if(!followingUser.getFollowing().contains(followedUser)) {
    		throw new RuntimeException("You have already don't follow this user!");
    	}
    	
    	// User that follow others
    	followingUser.getFollowing().remove(followedUser);
    	followingUser.setFollowingCount(Math.max(0, followingUser.getFollowingCount() - 1));
    	
    	// User that followed by others
    	followedUser.getFollowers().remove(followedUser);
    	followedUser.setFollowerCount(Math.max(0, followedUser.getFollowingCount() - 1));
    	
    	userRepository.save(followingUser);
    	userRepository.save(followedUser);
    	
    	return convertToUserProfileDto(followingUser);
    }

	@Override
	public Page<UserSummaryDto> findFollowersByUsername(String currentUsername, Pageable pageable) {
		Page<User> followers = userRepository.findFollowersByUsername(currentUsername, pageable);
		Page<UserSummaryDto> followersDto;
		
		if(!followers.isEmpty()) {
			followersDto = followers.map(this::convertToUserSummaryDto);
		}
		else {
			throw new ResourceNotFoundException("Followers not found!");
		}
		
		return followersDto;
	}

	@Override
	public Page<UserSummaryDto> findFollowingsByUsername(String currentUsername, Pageable pageable) {
		Page<User> followings = userRepository.findFollowingsByUsername(currentUsername, pageable);
		Page<UserSummaryDto> followingsDto;
		
		if(!followings.isEmpty()) {
			followingsDto = followings.map(this::convertToUserSummaryDto);
		}
		else {
			throw new ResourceNotFoundException("Followings not found!");
		}
		
		return followingsDto;
	}
}
