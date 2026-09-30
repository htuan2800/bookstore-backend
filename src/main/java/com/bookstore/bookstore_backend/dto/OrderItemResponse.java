package com.bookstore.bookstore_backend.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
    Long bookId,
    String bookTitle,
    Integer quantity,
    BigDecimal price,
    BigDecimal subtotal
) {}
