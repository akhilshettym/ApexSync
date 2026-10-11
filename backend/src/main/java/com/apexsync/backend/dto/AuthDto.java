package com.apexsync.backend.dto;

import com.apexsync.backend.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AuthDto {
    public record RegisterRequest(
            @Email @NotBlank String email,
            @NotBlank String password,
            @NotNull User.Role role
    ) {}

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record AuthResponse(
            String token,
            String email,
            String role
    ) {}
}