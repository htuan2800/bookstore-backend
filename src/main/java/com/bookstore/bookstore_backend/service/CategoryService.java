package com.bookstore.bookstore_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bookstore.bookstore_backend.dto.CategoryRequest;
import com.bookstore.bookstore_backend.entity.Category;
import com.bookstore.bookstore_backend.exception.ResourceNotFoundException;
import com.bookstore.bookstore_backend.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return categoryRepository.findByIsDeletedFalse();
    }

    public Category findById(Long id) {
        return categoryRepository.findByIdAndIsDeletedFalse(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại với ID: " + id));
    }

    public Category create(CategoryRequest req) {
        Category category = new Category();
        category.setName(req.name());
        category.setDescription(req.description());
        return categoryRepository.save(category);
    }

    public Category update(Long id, CategoryRequest req) {
        Category category = findById(id);
        category.setName(req.name());
        category.setDescription(req.description());
        return categoryRepository.save(category);
    }

    public void delete(Long id) {
        Category category = findById(id);
        category.setDeleted(true);
        categoryRepository.save(category);
    }
}
