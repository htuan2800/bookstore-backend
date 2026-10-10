package com.bookstore.bookstore_backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.bookstore_backend.dto.BookRequest;
import com.bookstore.bookstore_backend.dto.BookResponse;
import com.bookstore.bookstore_backend.entity.Author;
import com.bookstore.bookstore_backend.entity.Book;
import com.bookstore.bookstore_backend.entity.Category;
import com.bookstore.bookstore_backend.exception.ResourceNotFoundException;
import com.bookstore.bookstore_backend.repository.AuthorRepository;
import com.bookstore.bookstore_backend.repository.BookRepository;
import com.bookstore.bookstore_backend.repository.CategoryRepository;
import com.bookstore.bookstore_backend.specification.BookSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    @jakarta.annotation.PostConstruct
    public void onInit() {
        org.slf4j.LoggerFactory.getLogger(BookService.class)
            .info(">>> [Bean Lifecycle] BookService initialized successfully in ApplicationContext (Singleton scope).");
    }

    @jakarta.annotation.PreDestroy
    public void onDestroy() {
        org.slf4j.LoggerFactory.getLogger(BookService.class)
            .info(">>> [Bean Lifecycle] BookService is about to be destroyed as ApplicationContext closes.");
    }

    /**
     * Lấy danh sách toàn bộ sách có cache Redis.
     * Khi khách hàng truy cập nhiều, dữ liệu được lấy thẳng từ Redis với độ trễ thấp (~2-3ms).
     */
    @Cacheable(value = "books", key = "'all'")
    @Transactional(readOnly = true)
    public List<BookResponse> getAllBooks() {
        return bookRepository.findByIsDeletedFalse()
                .stream()
                .map(BookResponse::fromEntity)
                .toList();
    }

    /**
     * Phân trang tiêu chuẩn với Pageable (Tối ưu tài nguyên cho tập dữ liệu lớn).
     */
    @Transactional(readOnly = true)
    public Page<BookResponse> getBooksPaged(Pageable pageable) {
        return bookRepository.findByIsDeletedFalse(pageable)
                .map(BookResponse::fromEntity);
    }

    /**
     * Lấy chi tiết sách theo ID có cache Redis.
     */
    @Cacheable(value = "book_detail", key = "#id")
    @Transactional(readOnly = true)
    public Book findById(Long id) {
        return bookRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + id));
    }

    /**
     * Thêm sách mới: Tự động xóa cache để dữ liệu không bị cũ (Cache Eviction).
     */
    @CacheEvict(value = {"books", "book_detail"}, allEntries = true)
    public Book create(BookRequest req) {
        Book book = new Book();
        mapRequestToEntity(req, book);
        return bookRepository.save(book);
    }

    /**
     * Cập nhật sách: Xóa cache cũ.
     */
    @CacheEvict(value = {"books", "book_detail"}, allEntries = true)
    public Book update(Long id, BookRequest req) {
        Book book = findById(id);
        mapRequestToEntity(req, book);
        return bookRepository.save(book);
    }

    /**
     * Xóa mềm sách: Xóa cache cũ.
     */
    @CacheEvict(value = {"books", "book_detail"}, allEntries = true)
    public void delete(Long id) {
        Book book = findById(id);
        book.setDeleted(true);
        bookRepository.save(book);
    }

    private void mapRequestToEntity(BookRequest req, Book book) {
        book.setTitle(req.title());
        book.setPrice(req.price());
        book.setStockQuantity(req.stockQuantity());
        book.setDescription(req.description());
        book.setImageUrl(req.imageUrl());

        if (req.authorId() != null) {
            Author author = authorRepository.findById(req.authorId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Không tìm thấy tác giả với ID: " + req.authorId()));
            book.setAuthor(author);
        } else {
            book.setAuthor(null);
        }

        if (req.categoryId() != null) {
            Category category = categoryRepository.findById(req.categoryId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Không tìm thấy thể loại với ID: " + req.categoryId()));
            book.setCategory(category);
        } else {
            book.setCategory(null);
        }
    }

    @Transactional(readOnly = true)
    public List<BookResponse> searchBooks(String keyword) {
        return searchBooks(keyword, null, null, null, null);
    }

    @Transactional(readOnly = true)
    public List<BookResponse> searchBooks(String keyword, List<Long> categoryIds, List<Long> authorIds, BigDecimal minPrice, BigDecimal maxPrice) {
        Specification<Book> spec = BookSpecification.filterBooks(keyword, categoryIds, authorIds, minPrice, maxPrice);
        return bookRepository.findAll(spec)
                .stream()
                .map(BookResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<BookResponse> searchBooksPaged(
            String keyword,
            List<Long> categoryIds,
            List<Long> authorIds,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable) {
        Specification<Book> spec = BookSpecification.filterBooks(keyword, categoryIds, authorIds, minPrice, maxPrice);
        return bookRepository.findAll(spec, pageable)
                .map(BookResponse::fromEntity);
    }
}
