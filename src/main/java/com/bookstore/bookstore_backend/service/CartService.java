package com.bookstore.bookstore_backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.bookstore_backend.dto.CartItemRequest;
import com.bookstore.bookstore_backend.dto.CartItemResponse;
import com.bookstore.bookstore_backend.entity.Book;
import com.bookstore.bookstore_backend.entity.CartItem;
import com.bookstore.bookstore_backend.entity.User;
import com.bookstore.bookstore_backend.exception.InsufficientStockException;
import com.bookstore.bookstore_backend.exception.ResourceNotFoundException;
import com.bookstore.bookstore_backend.repository.BookRepository;
import com.bookstore.bookstore_backend.repository.CartItemRepository;
import com.bookstore.bookstore_backend.repository.UserRepository;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public CartService(CartItemRepository cartItemRepository,
                       BookRepository bookRepository,
                       UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    public List<CartItemResponse> getCart(String username) {
        User user = findUser(username);
        return cartItemRepository.findByUserId(user.getId()).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public CartItemResponse addToCart(String username, CartItemRequest req) {
        User user = findUser(username);
        Book book = bookRepository.findById(req.bookId())
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + req.bookId()));

        // Kiểm tra tồn kho
        if (book.getStockQuantity() < req.quantity()) {
            throw new InsufficientStockException(
                "Sách \"" + book.getTitle() + "\" chỉ còn " + book.getStockQuantity() + " cuốn trong kho");
        }

        // Nếu sách đã trong giỏ → cập nhật số lượng
        CartItem cartItem = cartItemRepository.findByUserIdAndBookId(user.getId(), book.getId())
            .orElse(null);

        if (cartItem != null) {
            int newQty = cartItem.getQuantity() + req.quantity();
            if (newQty > book.getStockQuantity()) {
                throw new InsufficientStockException(
                    "Tổng số lượng trong giỏ (" + newQty + ") vượt quá tồn kho (" + book.getStockQuantity() + ")");
            }
            cartItem.setQuantity(newQty);
        } else {
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setBook(book);
            cartItem.setQuantity(req.quantity());
        }

        return toResponse(cartItemRepository.save(cartItem));
    }

    @Transactional
    public CartItemResponse updateQuantity(String username, Long cartItemId, Integer quantity) {
        User user = findUser(username);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mục giỏ hàng"));

        // Kiểm tra quyền sở hữu
        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Không tìm thấy mục giỏ hàng");
        }

        // Kiểm tra tồn kho
        if (quantity > cartItem.getBook().getStockQuantity()) {
            throw new InsufficientStockException(
                "Sách \"" + cartItem.getBook().getTitle() + "\" chỉ còn " + cartItem.getBook().getStockQuantity() + " cuốn");
        }

        cartItem.setQuantity(quantity);
        return toResponse(cartItemRepository.save(cartItem));
    }

    @Transactional
    public void removeFromCart(String username, Long cartItemId) {
        User user = findUser(username);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mục giỏ hàng"));

        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Không tìm thấy mục giỏ hàng");
        }

        cartItemRepository.delete(cartItem);
    }

    private User findUser(String username) {
        return userRepository.findByUsernameOrEmail(username, username)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    private CartItemResponse toResponse(CartItem item) {
        Book book = item.getBook();
        BigDecimal subtotal = book.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartItemResponse(
            item.getId(),
            book.getId(),
            book.getTitle(),
            book.getAuthor() != null ? book.getAuthor().getName() : null,
            book.getPrice(),
            item.getQuantity(),
            subtotal,
            book.getStockQuantity()
        );
    }
}
