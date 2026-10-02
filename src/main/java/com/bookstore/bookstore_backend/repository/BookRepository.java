package com.bookstore.bookstore_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.lang.Nullable;

import com.bookstore.bookstore_backend.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    List<Book> findByIsDeletedFalse();
    Optional<Book> findByIdAndIsDeletedFalse(Long id);

    @EntityGraph(attributePaths = {"author", "category"})
    List<Book> findByTitleContainingIgnoreCase(String keyword);

    @Override
    @EntityGraph(attributePaths = {"author", "category"})
    List<Book> findAll(@Nullable Specification<Book> spec);
}


