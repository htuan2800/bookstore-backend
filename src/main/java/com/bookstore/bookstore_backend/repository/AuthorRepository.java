package com.bookstore.bookstore_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.bookstore_backend.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
