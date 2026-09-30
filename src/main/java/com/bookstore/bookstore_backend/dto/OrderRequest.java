package com.bookstore.bookstore_backend.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderRequest(
    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    String shippingAddress
) {}
