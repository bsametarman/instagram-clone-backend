package com.instaclone.InstagramClone.dto.user;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDto {
	@NotBlank(message = "Username or email cannot be blank")
    private String login;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    // Getters and Setters
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
