package com.bookstore.bookstore_backend.dto;

public record AuthResponse(
        String token,
        String username,
        String fullName,
        String role
) {
}