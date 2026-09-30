package com.bookstore.bookstore_backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookstore.bookstore_backend.dto.OrderItemResponse;
import com.bookstore.bookstore_backend.dto.OrderRequest;
import com.bookstore.bookstore_backend.dto.OrderResponse;
import com.bookstore.bookstore_backend.entity.Book;
import com.bookstore.bookstore_backend.entity.CartItem;
import com.bookstore.bookstore_backend.entity.Order;
import com.bookstore.bookstore_backend.entity.OrderItem;
import com.bookstore.bookstore_backend.entity.User;
import com.bookstore.bookstore_backend.exception.InsufficientStockException;
import com.bookstore.bookstore_backend.exception.ResourceNotFoundException;
import com.bookstore.bookstore_backend.repository.CartItemRepository;
import com.bookstore.bookstore_backend.repository.OrderRepository;
import com.bookstore.bookstore_backend.repository.UserRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        CartItemRepository cartItemRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    /**
     * Tạo đơn hàng từ giỏ hàng hiện tại.
     * Logic nghiệp vụ:
     * 1. Lấy tất cả items trong giỏ
     * 2. Kiểm tra tồn kho từng sách
     * 3. Trừ tồn kho
     * 4. Tính tổng tiền
     * 5. Tạo Order + OrderItems
     * 6. Xóa giỏ hàng
     */
    @Transactional
    public OrderResponse placeOrder(String username, OrderRequest req) {
        User user = findUser(username);

        // 1. Lấy giỏ hàng
        List<CartItem> cartItems = cartItemRepository.findByUserId(user.getId());
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng trống, không thể đặt hàng");
        }

        // 2. Kiểm tra tồn kho & trừ kho
        BigDecimal totalAmount = BigDecimal.ZERO;

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(req.shippingAddress());
        order.setStatus("PENDING");

        for (CartItem cartItem : cartItems) {
            Book book = cartItem.getBook();

            // Kiểm tra tồn kho
            if (book.getStockQuantity() < cartItem.getQuantity()) {
                throw new InsufficientStockException(
                    "Sách \"" + book.getTitle() + "\" chỉ còn " + book.getStockQuantity()
                    + " cuốn, nhưng giỏ hàng yêu cầu " + cartItem.getQuantity() + " cuốn");
            }

            // Trừ tồn kho
            book.setStockQuantity(book.getStockQuantity() - cartItem.getQuantity());

            // Tạo OrderItem (snapshot giá tại thời điểm đặt)
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(book);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(book.getPrice());
            order.getItems().add(orderItem);

            // Tính tổng
            totalAmount = totalAmount.add(
                book.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
        }

        order.setTotalAmount(totalAmount);

        // 3. Lưu đơn hàng (cascade lưu OrderItems)
        Order savedOrder = orderRepository.save(order);

        // 4. Xóa giỏ hàng
        cartItemRepository.deleteByUserId(user.getId());

        return toResponse(savedOrder);
    }

    public List<OrderResponse> getMyOrders(String username) {
        User user = findUser(username);
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
            .map(this::toResponse)
            .toList();
    }

    public OrderResponse getOrderById(String username, Long orderId) {
        User user = findUser(username);
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        // Customer chỉ được xem đơn của mình
        if (!order.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId);
        }

        return toResponse(order);
    }

    // === ADMIN ===

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng với ID: " + orderId));

        List<String> validStatuses = List.of("PENDING", "CONFIRMED", "SHIPPING", "DELIVERED", "CANCELLED");
        if (!validStatuses.contains(status)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ: " + status);
        }

        order.setStatus(status);
        return toResponse(orderRepository.save(order));
    }

    private User findUser(String username) {
        return userRepository.findByUsernameOrEmail(username, username)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
            .map(item -> new OrderItemResponse(
                item.getBook().getId(),
                item.getBook().getTitle(),
                item.getQuantity(),
                item.getPrice(),
                item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            ))
            .toList();

        return new OrderResponse(
            order.getId(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getShippingAddress(),
            order.getCreatedAt(),
            items
        );
    }
}
