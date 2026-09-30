package com.bookstore.bookstore_backend.dto;

import java.math.BigDecimal;

public record CartItemResponse(
    Long id,
    Long bookId,
    String bookTitle,
    String authorName,
    BigDecimal bookPrice,
    Integer quantity,
    BigDecimal subtotal,
    Integer stockQuantity
) {}
