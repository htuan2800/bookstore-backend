package com.bookstore.bookstore_backend.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class PrototypeScopeDemo {
    // Bean dùng để thử nghiệm cơ chế tạo instance mới
}