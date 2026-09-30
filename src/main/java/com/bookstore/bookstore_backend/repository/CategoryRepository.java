package com.bookstore.bookstore_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.bookstore_backend.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
