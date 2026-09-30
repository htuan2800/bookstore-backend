package com.bookstore.bookstore_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bookstore.bookstore_backend.dto.AuthorRequest;
import com.bookstore.bookstore_backend.entity.Author;
import com.bookstore.bookstore_backend.exception.ResourceNotFoundException;
import com.bookstore.bookstore_backend.repository.AuthorRepository;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<Author> findAll() {
        return authorRepository.findByIsDeletedFalse();
    }

    public Author findById(Long id) {
        return authorRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả với ID: " + id));
    }

    public Author create(AuthorRequest req) {
        Author author = new Author();
        author.setName(req.name());
        author.setBio(req.bio());
        return authorRepository.save(author);
    }

    public Author update(Long id, AuthorRequest req) {
        Author author = findById(id);
        author.setName(req.name());
        author.setBio(req.bio());
        return authorRepository.save(author);
    }

    public void delete(Long id) {
        Author author = findById(id);
        author.setDeleted(true);
        authorRepository.save(author);
    }
}
