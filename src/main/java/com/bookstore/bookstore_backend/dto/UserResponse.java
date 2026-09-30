package com.bookstore.bookstore_backend.dto;

import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String username,
    String email,
    String fullName,
    String role,
    LocalDateTime createdAt
) {}
