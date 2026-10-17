package com.apexsync.backend.service;

import com.apexsync.backend.dto.AuthDto;
import com.apexsync.backend.entity.User;
import com.apexsync.backend.repository.UserRepository;
import com.apexsync.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public User register(AuthDto.RegisterRequest request) {
        User user = User.builder()
                .email(request.email())
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();
        return userRepository.save(user);
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        User user = userRepository.findAll().stream()
                .filter(u -> u.getEmail().equals(request.email()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = tokenProvider.generateToken(user.getEmail(), user.getRole().name());
        String jwtToken = tokenProvider.generateToken(user.getEmail(), user.getRole().name());

        return new AuthDto.AuthResponse(jwtToken, user.getEmail(), user.getRole().name());
    }
}