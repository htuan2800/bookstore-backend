package com.bookstore.bookstore_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.bookstore_backend.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByIsDeletedFalse();
    Optional<Book> findByIdAndIsDeletedFalse(Long id);
}
