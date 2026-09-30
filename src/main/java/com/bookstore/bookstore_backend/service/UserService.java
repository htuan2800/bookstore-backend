package com.bookstore.bookstore_backend.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bookstore.bookstore_backend.dto.UserCreateRequest;
import com.bookstore.bookstore_backend.dto.UserResponse;
import com.bookstore.bookstore_backend.entity.User;
import com.bookstore.bookstore_backend.exception.ResourceNotFoundException;
import com.bookstore.bookstore_backend.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse create(UserCreateRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại trong hệ thống");
        }
        if (userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email đã được đăng ký trong hệ thống");
        }

        User user = new User();
        user.setUsername(req.username().trim());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setEmail(req.email().trim());
        user.setFullName(req.fullName() != null && !req.fullName().isBlank() ? req.fullName().trim() : null);

        String role = req.role();
        if (role == null || role.isBlank()) {
            role = "ROLE_CUSTOMER";
        } else if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role.toUpperCase();
        }
        user.setRole(role);
        user.setDeleted(false);

        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    public List<UserResponse> findAll() {
        return userRepository.findByIsDeletedFalse().stream()
            .map(this::mapToResponse)
            .toList();
    }

    public UserResponse findById(Long id) {
        return userRepository.findById(id)
            .map(this::mapToResponse)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));
    }

    public UserResponse updateRole(Long id, String role, String currentUsername) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));

        // 1. Chặn tự đổi vai trò của chính mình
        if (user.getUsername().equalsIgnoreCase(currentUsername)) {
            throw new IllegalArgumentException("Quản trị viên không thể tự thay đổi vai trò của chính mình");
        }

        // 2. Chặn thay đổi vai trò của tài khoản admin hệ thống gốc
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            throw new IllegalArgumentException("Không thể thay đổi vai trò của tài khoản Quản trị viên hệ thống (admin)");
        }

        // 3. Chặn thay đổi tài khoản có cùng quyền ROLE_ADMIN
        if ("ROLE_ADMIN".equals(user.getRole())) {
            throw new IllegalArgumentException("Không thể thay đổi vai trò của tài khoản có cùng quyền Quản trị viên");
        }

        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role.toUpperCase();
        }
        user.setRole(role);
        User saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    public void delete(Long id, String currentUsername) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + id));

        // 1. Chặn tự xóa tài khoản của chính mình
        if (user.getUsername().equalsIgnoreCase(currentUsername)) {
            throw new IllegalArgumentException("Quản trị viên không thể tự xóa tài khoản của chính mình");
        }

        // 2. Chặn xóa tài khoản admin hệ thống gốc
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            throw new IllegalArgumentException("Không thể xóa tài khoản Quản trị viên hệ thống (admin)");
        }

        // 3. Chặn xóa tài khoản có cùng quyền ROLE_ADMIN
        if ("ROLE_ADMIN".equals(user.getRole())) {
            throw new IllegalArgumentException("Không thể xóa tài khoản có cùng quyền Quản trị viên");
        }

        user.setDeleted(true);
        userRepository.save(user);
    }

    private UserResponse mapToResponse(User u) {
        return new UserResponse(
            u.getId(),
            u.getUsername(),
            u.getEmail(),
            u.getFullName(),
            u.getRole(),
            u.getCreatedAt()
        );
    }
}
