package com.bookstore.bookstore_backend.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bookstore.bookstore_backend.dto.AuthResponse;
import com.bookstore.bookstore_backend.dto.LoginRequest;
import com.bookstore.bookstore_backend.dto.RegisterRequest;
import com.bookstore.bookstore_backend.entity.User;
import com.bookstore.bookstore_backend.repository.UserRepository;
import com.bookstore.bookstore_backend.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public void register(RegisterRequest req) {
        if (!req.password().equals(req.confirmPassword()))
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
        if (userRepository.existsByUsername(req.username()))
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
        if (userRepository.existsByEmail(req.email()))
            throw new IllegalArgumentException("Email đã được đăng ký");

        User user = new User();
        user.setUsername(req.username());
        user.setFullName(req.fullName());
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.identifier(), req.password()));
        User user = userRepository.findByUsernameOrEmail(req.identifier(), req.identifier()).orElseThrow();
        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return new AuthResponse(token, user.getUsername(), user.getFullName(), user.getRole());
    }
}