package com.bookstore.bookstore_backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

import com.bookstore.bookstore_backend.dto.AuthorRequest;
import com.bookstore.bookstore_backend.dto.BookRequest;
import com.bookstore.bookstore_backend.dto.CategoryRequest;
import com.bookstore.bookstore_backend.dto.OrderResponse;
import com.bookstore.bookstore_backend.dto.UserCreateRequest;
import com.bookstore.bookstore_backend.dto.UserResponse;
import com.bookstore.bookstore_backend.entity.Author;
import com.bookstore.bookstore_backend.entity.Book;
import com.bookstore.bookstore_backend.entity.Category;
import com.bookstore.bookstore_backend.service.AuthorService;
import com.bookstore.bookstore_backend.service.BookService;
import com.bookstore.bookstore_backend.service.CategoryService;
import com.bookstore.bookstore_backend.service.OrderService;
import com.bookstore.bookstore_backend.service.UserService;

import jakarta.validation.Valid;

/**
 * API quản trị — chỉ dành cho ROLE_ADMIN.
 * Quản lý Sách, Đơn hàng, Thể loại, Tác giả và Người dùng.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final BookService bookService;
    private final OrderService orderService;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final UserService userService;

    public AdminController(BookService bookService,
                           OrderService orderService,
                           AuthorService authorService,
                           CategoryService categoryService,
                           UserService userService) {
        this.bookService = bookService;
        this.orderService = orderService;
        this.authorService = authorService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    // === QUẢN LÝ SÁCH ===

    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookService.findAll());
    }

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

    // === QUẢN LÝ TÁC GIẢ (AUTHORS) ===

    @GetMapping("/authors")
    public ResponseEntity<List<Author>> getAllAuthors() {
        return ResponseEntity.ok(authorService.findAll());
    }

    @GetMapping("/authors/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.findById(id));
    }

    @PostMapping("/authors")
    public ResponseEntity<Author> createAuthor(@Valid @RequestBody AuthorRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authorService.create(req));
    }

    @PutMapping("/authors/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorRequest req) {
        return ResponseEntity.ok(authorService.update(id, req));
    }

    @DeleteMapping("/authors/{id}")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // === QUẢN LÝ THỂ LOẠI (CATEGORIES) ===

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(req));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest req) {
        return ResponseEntity.ok(categoryService.update(id, req));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // === QUẢN LÝ NGƯỜI DÙNG (USERS) ===

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(req));
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Long id,
            @RequestParam String role,
            @AuthenticationPrincipal UserDetails currentUser) {
        String currentUsername = currentUser != null ? currentUser.getUsername() : "";
        return ResponseEntity.ok(userService.updateRole(id, role, currentUsername));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails currentUser) {
        String currentUsername = currentUser != null ? currentUser.getUsername() : "";
        userService.delete(id, currentUsername);
        return ResponseEntity.noContent().build();
    }
}
