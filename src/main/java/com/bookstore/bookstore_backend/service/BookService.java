package com.bookstore.bookstore_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bookstore.bookstore_backend.dto.BookRequest;
import com.bookstore.bookstore_backend.entity.Author;
import com.bookstore.bookstore_backend.entity.Book;
import com.bookstore.bookstore_backend.entity.Category;
import com.bookstore.bookstore_backend.exception.ResourceNotFoundException;
import com.bookstore.bookstore_backend.repository.AuthorRepository;
import com.bookstore.bookstore_backend.repository.BookRepository;
import com.bookstore.bookstore_backend.repository.CategoryRepository;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    public BookService(BookRepository bookRepository,
                       AuthorRepository authorRepository,
                       CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findByIsDeletedFalse();
    }

    public Book findById(Long id) {
        return bookRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + id));
    }

    public Book create(BookRequest req) {
        Book book = new Book();
        mapRequestToEntity(req, book);
        return bookRepository.save(book);
    }

    public Book update(Long id, BookRequest req) {
        Book book = findById(id);
        mapRequestToEntity(req, book);
        return bookRepository.save(book);
    }

    public void delete(Long id) {
        Book book = findById(id);
        book.setDeleted(true);
        bookRepository.save(book);
    }

    private void mapRequestToEntity(BookRequest req, Book book) {
        book.setTitle(req.title());
        book.setPrice(req.price());
        book.setStockQuantity(req.stockQuantity());
        book.setDescription(req.description());
        book.setImageUrl(req.imageUrl());

        if (req.authorId() != null) {
            Author author = authorRepository.findById(req.authorId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả với ID: " + req.authorId()));
            book.setAuthor(author);
        } else {
            book.setAuthor(null);
        }

        if (req.categoryId() != null) {
            Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại với ID: " + req.categoryId()));
            book.setCategory(category);
        } else {
            book.setCategory(null);
        }
    }
}
