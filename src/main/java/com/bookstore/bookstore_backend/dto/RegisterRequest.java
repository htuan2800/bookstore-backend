package com.bookstore.bookstore_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record RegisterRequest(
    @NotBlank @Size(min = 3, max = 50) String username,
    @NotBlank String fullName,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8, message = "Mật khẩu tối thiểu 8 ký tự") String password,
    @NotBlank String confirmPassword) {}