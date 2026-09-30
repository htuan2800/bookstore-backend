package com.bookstore.bookstore_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookstore.bookstore_backend.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
    List<Author> findByIsDeletedFalse();
    Optional<Author> findByIdAndIsDeletedFalse(Long id);
}
