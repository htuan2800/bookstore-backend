package com.bookstore.bookstore_backend.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bookstore.bookstore_backend.dto.AuthorResponse;
import com.bookstore.bookstore_backend.dto.BookResponse;
import com.bookstore.bookstore_backend.dto.CategoryResponse;
import com.bookstore.bookstore_backend.exception.GlobalExceptionHandler;
import com.bookstore.bookstore_backend.exception.ResourceNotFoundException;
import com.bookstore.bookstore_backend.security.CustomUserDetailsService;
import com.bookstore.bookstore_backend.security.JwtService;
import com.bookstore.bookstore_backend.service.AuthorService;
import com.bookstore.bookstore_backend.service.BookService;
import com.bookstore.bookstore_backend.service.CategoryService;

@WebMvcTest(controllers = BookController.class)
@Import(GlobalExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/books: Trả về danh sách sách 200 OK")
    void getAllBooks_ReturnsBookList() throws Exception {
        AuthorResponse author = new AuthorResponse(1L, "Tô Hoài", "Tác giả", false);
        CategoryResponse category = new CategoryResponse(1L, "Văn học thiếu nhi", "Mô tả", false);
        BookResponse response = new BookResponse(
            1L,
            "Dế Mèn Phiêu Lưu Ký",
            new BigDecimal("45000.00"),
            10,
            "Mô tả sách",
            "image.jpg",
            author,
            category,
            LocalDateTime.now(),
            false
        );

        when(bookService.getAllBooks()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Dế Mèn Phiêu Lưu Ký"))
                .andExpect(jsonPath("$[0].author.name").value("Tô Hoài"))
                .andExpect(jsonPath("$[0].category.name").value("Văn học thiếu nhi"));
    }

    @Test
    @DisplayName("GET /api/books/{id}: Ném 404 khi không tìm thấy sách")
    void getBookById_NotFound_Returns404() throws Exception {
        when(bookService.findById(999L))
                .thenThrow(new ResourceNotFoundException("Không tìm thấy sách với ID: 999"));

        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Không tìm thấy sách với ID: 999"));
    }
}
