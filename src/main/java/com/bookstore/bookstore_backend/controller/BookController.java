package com.bookstore.bookstore_backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.bookstore_backend.dto.BookResponse;
import com.bookstore.bookstore_backend.entity.Book;
import com.bookstore.bookstore_backend.service.BookService;

import lombok.RequiredArgsConstructor;

/**
 * API công khai để xem danh sách sách.
 * Controller chỉ tiếp nhận HTTP request, logic nằm ở BookService.
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor 
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        Book book = bookService.findById(id);
        return ResponseEntity.ok(BookResponse.fromEntity(book));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> searchBooks(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(bookService.searchBooks(keyword));
    }
}
