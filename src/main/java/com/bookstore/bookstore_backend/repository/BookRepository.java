package com.bookstore.bookstore_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.bookstore_backend.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
