package com.bookstore.bookstore_backend.dto;

import com.bookstore.bookstore_backend.entity.Book;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookResponse(
    Long id,
    String title,
    BigDecimal price,
    Integer stockQuantity,
    String description,
    String imageUrl,
    AuthorResponse author,
    CategoryResponse category,
    LocalDateTime createdAt,
    boolean isDeleted
) {
    public static BookResponse fromEntity(Book book) {
        if (book == null) return null;
        return new BookResponse(
            book.getId(),
            book.getTitle(),
            book.getPrice(),
            book.getStockQuantity(),
            book.getDescription(),
            book.getImageUrl(),
            AuthorResponse.fromEntity(book.getAuthor()),
            CategoryResponse.fromEntity(book.getCategory()),
            book.getCreatedAt(),
            book.isDeleted()
        );
    }
}