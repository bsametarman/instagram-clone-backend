package com.instaclone.InstagramClone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.instaclone.InstagramClone.dto.user.JwtResponseDto;
import com.instaclone.InstagramClone.dto.user.LoginRequestDto;
import com.instaclone.InstagramClone.dto.user.SignUpRequestDto;
import com.instaclone.InstagramClone.security.UserPrincipal;
import com.instaclone.InstagramClone.security.jwt.JwtTokenProvider;
import com.instaclone.InstagramClone.service.user.UserService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtResponseDto> authenticateUser(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            loginRequestDto.getLogin(),
            loginRequestDto.getPassword()
        );

        Authentication authentication = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        return ResponseEntity.ok(new JwtResponseDto(
                jwt,
                userPrincipal.getId(),
                userPrincipal.getUsername(),
                userPrincipal.getEmail()
        ));
    }
    
    @PostMapping("/signup")
    public ResponseEntity<JwtResponseDto> registerUser(@Valid @RequestBody SignUpRequestDto signUpRequestDto) {
        JwtResponseDto response = userService.registerUser(signUpRequestDto);
        
        return ResponseEntity.ok(response);
    }
}
