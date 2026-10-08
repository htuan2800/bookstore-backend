package com.bookstore.bookstore_backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookstore.bookstore_backend.dto.OrderRequest;
import com.bookstore.bookstore_backend.dto.OrderResponse;
import com.bookstore.bookstore_backend.entity.Book;
import com.bookstore.bookstore_backend.entity.CartItem;
import com.bookstore.bookstore_backend.entity.Order;
import com.bookstore.bookstore_backend.entity.User;
import com.bookstore.bookstore_backend.exception.InsufficientStockException;
import com.bookstore.bookstore_backend.repository.CartItemRepository;
import com.bookstore.bookstore_backend.repository.OrderRepository;
import com.bookstore.bookstore_backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User sampleUser;
    private Book sampleBook;
    private CartItem sampleCartItem;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("customer1");
        sampleUser.setEmail("customer1@test.com");

        sampleBook = new Book();
        sampleBook.setId(10L);
        sampleBook.setTitle("Dế Mèn Phiêu Lưu Ký");
        sampleBook.setPrice(new BigDecimal("50000.00"));
        sampleBook.setStockQuantity(10);

        sampleCartItem = new CartItem();
        sampleCartItem.setId(100L);
        sampleCartItem.setUser(sampleUser);
        sampleCartItem.setBook(sampleBook);
        sampleCartItem.setQuantity(2);
    }

    @Test
    @DisplayName("Đặt hàng thành công khi tồn kho đủ: trừ kho, lưu đơn hàng, xóa giỏ hàng")
    void placeOrder_Success() {
        when(userRepository.findByUsernameOrEmail("customer1", "customer1"))
                .thenReturn(Optional.of(sampleUser));
        when(cartItemRepository.findByUserId(1L))
                .thenReturn(List.of(sampleCartItem));
        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order o = invocation.getArgument(0);
                    o.setId(999L);
                    return o;
                });

        OrderRequest request = new OrderRequest("123 Nguyễn Văn Cừ, Q5, TP.HCM");
        OrderResponse response = orderService.placeOrder("customer1", request);

        assertNotNull(response);
        assertEquals(999L, response.id());
        assertEquals("PENDING", response.status());
        assertEquals(new BigDecimal("100000.00"), response.totalAmount());
        assertEquals(8, sampleBook.getStockQuantity(), "Tồn kho sách phải giảm từ 10 xuống 8");

        verify(orderRepository).save(any(Order.class));
        verify(cartItemRepository).deleteByUserId(1L);
    }

    @Test
    @DisplayName("Đặt hàng thất bại: Ném IllegalArgumentException khi giỏ hàng trống")
    void placeOrder_ThrowsException_WhenCartIsEmpty() {
        when(userRepository.findByUsernameOrEmail("customer1", "customer1"))
                .thenReturn(Optional.of(sampleUser));
        when(cartItemRepository.findByUserId(1L))
                .thenReturn(new ArrayList<>());

        OrderRequest request = new OrderRequest("123 Nguyễn Văn Cừ");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            orderService.placeOrder("customer1", request);
        });

        assertEquals("Giỏ hàng trống, không thể đặt hàng", ex.getMessage());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("Đặt hàng thất bại: Ném InsufficientStockException khi số lượng yêu cầu vượt quá tồn kho")
    void placeOrder_ThrowsException_WhenStockIsInsufficient() {
        sampleBook.setStockQuantity(1); // Chỉ còn 1 cuốn nhưng giỏ hàng có 2 cuốn
        when(userRepository.findByUsernameOrEmail("customer1", "customer1"))
                .thenReturn(Optional.of(sampleUser));
        when(cartItemRepository.findByUserId(1L))
                .thenReturn(List.of(sampleCartItem));

        OrderRequest request = new OrderRequest("123 Nguyễn Văn Cừ");

        InsufficientStockException ex = assertThrows(InsufficientStockException.class, () -> {
            orderService.placeOrder("customer1", request);
        });

        assertEquals(1, sampleBook.getStockQuantity(), "Tồn kho không được thay đổi khi giao dịch lỗi");
        verify(orderRepository, never()).save(any(Order.class));
        verify(cartItemRepository, never()).deleteByUserId(any());
    }

    @Test
    @DisplayName("Cập nhật trạng thái đơn hàng: Ném IllegalArgumentException khi trạng thái không hợp lệ")
    void updateOrderStatus_ThrowsException_WhenStatusInvalid() {
        Order sampleOrder = new Order();
        sampleOrder.setId(1L);
        sampleOrder.setStatus("PENDING");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(sampleOrder));

        assertThrows(IllegalArgumentException.class, () -> {
            orderService.updateOrderStatus(1L, "STATUS_KHONG_HOP_LE");
        });
    }
}
