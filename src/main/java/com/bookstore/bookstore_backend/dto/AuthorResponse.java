package com.bookstore.bookstore_backend.dto;
import com.bookstore.bookstore_backend.entity.Author;

public record AuthorResponse(
    Long id,
    String name,
    String bio,
    boolean isDeleted
) {
    public static AuthorResponse fromEntity(Author author) {
        if (author == null) return null;
        return new AuthorResponse(
            author.getId(),
            author.getName(),
            author.getBio(),
            author.isDeleted()
        );
    }
}