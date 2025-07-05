package com.pioneer.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LoginRequest {
    @NotBlank(message = "Login must not be blank")
    private String login;

    @NotBlank(message = "Password must not be blank")
    private String password;
}
