package com.bookstore.bookstore_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.bookstore_backend.dto.CartItemRequest;
import com.bookstore.bookstore_backend.dto.CartItemResponse;
import com.bookstore.bookstore_backend.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * API giỏ hàng — chỉ dành cho ROLE_CUSTOMER (đã xác thực).
 * Controller chỉ tiếp nhận request và truyền sang CartService.
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor 
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartItemResponse>> getCart(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(cartService.getCart(user.getUsername()));
    }

    @PostMapping
    public ResponseEntity<CartItemResponse> addToCart(
            @AuthenticationPrincipal UserDetails user,
            @Valid @RequestBody CartItemRequest req) {
        return ResponseEntity.ok(cartService.addToCart(user.getUsername(), req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItemResponse> updateQuantity(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateQuantity(user.getUsername(), id, quantity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFromCart(
            @AuthenticationPrincipal UserDetails user,
            @PathVariable Long id) {
        cartService.removeFromCart(user.getUsername(), id);
        return ResponseEntity.noContent().build();
    }
}
