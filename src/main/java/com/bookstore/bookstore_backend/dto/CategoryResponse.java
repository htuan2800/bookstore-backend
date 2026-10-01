package com.bookstore.bookstore_backend.dto;
import com.bookstore.bookstore_backend.entity.Category;

public record CategoryResponse(
    Long id,
    String name,
    String description,
    boolean isDeleted
) {
    public static CategoryResponse fromEntity(Category category) {
        if (category == null) return null;
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.isDeleted()
        );
    }
}