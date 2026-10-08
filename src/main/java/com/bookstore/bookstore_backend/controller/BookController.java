package com.bookstore.bookstore_backend.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookstore.bookstore_backend.dto.BookResponse;
import com.bookstore.bookstore_backend.entity.Author;
import com.bookstore.bookstore_backend.entity.Book;
import com.bookstore.bookstore_backend.entity.Category;
import com.bookstore.bookstore_backend.service.AuthorService;
import com.bookstore.bookstore_backend.service.BookService;
import com.bookstore.bookstore_backend.service.CategoryService;

import lombok.RequiredArgsConstructor;

/**
 * API công khai để xem và tìm kiếm danh sách sách, thể loại, tác giả.
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor 
public class BookController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<BookResponse>> getBooksPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction dir = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));
        return ResponseEntity.ok(bookService.getBooksPaged(pageable));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getPublicCategories() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @GetMapping("/authors")
    public ResponseEntity<List<Author>> getPublicAuthors() {
        return ResponseEntity.ok(authorService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        Book book = bookService.findById(id);
        return ResponseEntity.ok(BookResponse.fromEntity(book));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> searchBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> authorIds,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return ResponseEntity.ok(bookService.searchBooks(keyword, categoryIds, authorIds, minPrice, maxPrice));
    }

    /**
     * API tìm kiếm và lọc sách có hỗ trợ phân trang chuẩn Pageable.
     */
    @GetMapping("/search/paged")
    public ResponseEntity<Page<BookResponse>> searchBooksPaged(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<Long> categoryIds,
            @RequestParam(required = false) List<Long> authorIds,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction dir = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));
        return ResponseEntity.ok(bookService.searchBooksPaged(keyword, categoryIds, authorIds, minPrice, maxPrice, pageable));
    }
}
