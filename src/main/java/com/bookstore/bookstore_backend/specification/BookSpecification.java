package com.bookstore.bookstore_backend.specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.bookstore.bookstore_backend.entity.Book;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class BookSpecification {

    /**
     * Tạo Specification lọc động chuẩn Spring Data JPA.
     * Tự động bổ sung các Predicate tương ứng với tham số truyền vào.
     */
    public static Specification<Book> filterBooks(
            String keyword,
            List<Long> categoryIds,
            List<Long> authorIds,
            BigDecimal minPrice,
            BigDecimal maxPrice) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("isDeleted"), false));

            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
                Predicate authorMatch = cb.like(cb.lower(root.join("author", JoinType.LEFT).get("name")), pattern);
                predicates.add(cb.or(titleMatch, authorMatch));
            }

            if (categoryIds != null && !categoryIds.isEmpty()) {
                predicates.add(root.get("category").get("id").in(categoryIds));
            }

            if (authorIds != null && !authorIds.isEmpty()) {
                predicates.add(root.get("author").get("id").in(authorIds));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
