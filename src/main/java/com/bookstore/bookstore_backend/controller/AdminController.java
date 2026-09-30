package com.bookstore.bookstore_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.bookstore_backend.dto.BookRequest;
import com.bookstore.bookstore_backend.dto.OrderResponse;
import com.bookstore.bookstore_backend.entity.Book;
import com.bookstore.bookstore_backend.service.BookService;
import com.bookstore.bookstore_backend.service.OrderService;

import jakarta.validation.Valid;

/**
 * API quản trị — chỉ dành cho ROLE_ADMIN.
 * Quản lý sách (CRUD) và quản lý đơn hàng (xem tất cả, cập nhật trạng thái).
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final BookService bookService;
    private final OrderService orderService;

    public AdminController(BookService bookService, OrderService orderService) {
        this.bookService = bookService;
        this.orderService = orderService;
    }

    // === QUẢN LÝ SÁCH ===

    @PostMapping("/books")
    public ResponseEntity<Book> createBook(@Valid @RequestBody BookRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(req));
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest req) {
        return ResponseEntity.ok(bookService.update(id, req));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // === QUẢN LÝ ĐƠN HÀNG ===

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }
}
