package com.bookstore.bookstore_backend.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bookstore.bookstore_backend.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    public CustomUserDetailsService(UserRepository userRepository) { this.userRepository = userRepository; }

    @Override
    public UserDetails loadUserByUsername(String identifier) {
        com.bookstore.bookstore_backend.entity.User u = userRepository
            .findByUsernameOrEmail(identifier, identifier)
            .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản"));
        return new org.springframework.security.core.userdetails.User(
            u.getUsername(), u.getPassword(),
            List.of(new SimpleGrantedAuthority(u.getRole())));
    }
}