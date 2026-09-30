package com.bookstore.bookstore_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
    Long id,
    BigDecimal totalAmount,
    String status,
    String shippingAddress,
    LocalDateTime createdAt,
    List<OrderItemResponse> items
) {}
