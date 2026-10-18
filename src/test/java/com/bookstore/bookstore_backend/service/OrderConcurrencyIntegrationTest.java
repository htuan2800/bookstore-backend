package com.bookstore.bookstore_backend.service;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.bookstore.bookstore_backend.exception.InsufficientStockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.bookstore.bookstore_backend.dto.CartItemRequest;
import com.bookstore.bookstore_backend.dto.OrderRequest;
import com.bookstore.bookstore_backend.entity.Book;
import com.bookstore.bookstore_backend.entity.User;
import com.bookstore.bookstore_backend.repository.BookRepository;
import com.bookstore.bookstore_backend.repository.CartItemRepository;
import com.bookstore.bookstore_backend.repository.OrderRepository;
import com.bookstore.bookstore_backend.repository.UserRepository;

@SpringBootTest
class OrderConcurrencyIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    private Long testBookId;

    @BeforeEach
    void setUp() {
        // Dọn dẹp dữ liệu test cũ
        orderRepository.deleteAll();
        cartItemRepository.deleteAll();

        // 1. Tạo 1 cuốn sách duy nhất có tồn kho = 1
        Book book = new Book();
        book.setTitle("Sách Tranh Chấp Cuối Cùng");
        book.setPrice(new BigDecimal("100000.00"));
        book.setStockQuantity(1); // Chỉ còn đúng 1 cuốn!
        book.setVersion(0L);
        book = bookRepository.save(book);
        testBookId = book.getId();

        // 2. Tạo 2 tài khoản người dùng test
        createUserIfNotExists("user_concurrency_1", "user1@test.com");
        createUserIfNotExists("user_concurrency_2", "user2@test.com");

        // 3. Cả 2 user đều thêm cuốn sách này vào giỏ hàng
        cartService.addToCart("user_concurrency_1", new CartItemRequest(testBookId, 1));
        cartService.addToCart("user_concurrency_2", new CartItemRequest(testBookId, 1));
    }

    @Test
    @DisplayName("Thực nghiệm Concurrency: 2 request tranh mua cuốn sách cuối cùng đồng thời")
    void testConcurrentOrder_PreventsOverselling() throws InterruptedException {
        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch readyLatch = new CountDownLatch(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        String[] users = {"user_concurrency_1", "user_concurrency_2"};

        for (int i = 0; i < numberOfThreads; i++) {
            final String username = users[i];
            executorService.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await(); // Đợi cả 2 luồng sẵn sàng để bắn request 
                    orderService.placeOrder(username, new OrderRequest("123 Nguyễn Văn Cừ, Q5, TP.HCM"));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet(); 
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown(); // Bắn 2 request đồng thời
        doneLatch.await();
        executorService.shutdown();

        assertEquals(1, successCount.get(), "Chỉ duy nhất 1 khách hàng đặt thành công");
        assertEquals(1, failCount.get(), "Khách hàng còn lại phải nhận ngoại lệ thất bại (không bán vượt)");

        Book updatedBook = bookRepository.findById(testBookId).orElseThrow();
        assertEquals(0, updatedBook.getStockQuantity(), "Tồn kho sách trong database phải về đúng 0 cuốn (không bị âm)");
        assertEquals(1, orderRepository.count(), "Chỉ có đúng 1 đơn hàng được tạo trong bảng orders");
    }

    @Test
    @DisplayName("Thực nghiệm Rollback: Ngoại lệ xảy ra giữa chừng phải rollback sạch toàn bộ DB")
    void testOrderRollback_WhenExceptionOccurs_DatabaseStateIsPreserved() {
        orderRepository.deleteAll();
        cartItemRepository.deleteAll();

        Book book1 = new Book();
        book1.setTitle("Sách Rollback Test 1");
        book1.setPrice(new BigDecimal("50000.00"));
        book1.setStockQuantity(10);
        book1.setVersion(0L);
        book1 = bookRepository.save(book1);

        Book book2 = new Book();
        book2.setTitle("Sách Rollback Test 2");
        book2.setPrice(new BigDecimal("50000.00"));
        book2.setStockQuantity(5); // Ban đầu đủ 5 cuốn để thêm vào giỏ hàng thành công
        book2.setVersion(0L);
        book2 = bookRepository.save(book2);

        createUserIfNotExists("user_rollback_test", "rollback@test.com");

        // Giỏ hàng gồm: 2 cuốn Sách 1 và 5 cuốn Sách 2
        cartService.addToCart("user_rollback_test", new CartItemRequest(book1.getId(), 2));
        cartService.addToCart("user_rollback_test", new CartItemRequest(book2.getId(), 5));

        // Mô phỏng thực tế: Trước khi ấn Đặt hàng, sách 2 bị tranh mua dẫn đến chỉ còn 1 cuốn trong kho
        book2.setStockQuantity(1);
        bookRepository.save(book2);

        // Bắt ngoại lệ khi đặt hàng
        assertThrows(InsufficientStockException.class, () -> {
            orderService.placeOrder("user_rollback_test", new OrderRequest("Địa chỉ test rollback"));
        });

        // KIỂM CHỨNG TÍNH TOÀN VẸN
        // 1. Sách 1 không bị trừ kho dở dang (vẫn nguyên 10 cuốn)
        Book reloadedBook1 = bookRepository.findById(book1.getId()).orElseThrow();
        assertEquals(10, reloadedBook1.getStockQuantity(), "Tồn kho Sách 1 phải giữ nguyên 10 cuốn sau rollback");

        // 2. Sách 2 không đổi (vẫn 1 cuốn)
        Book reloadedBook2 = bookRepository.findById(book2.getId()).orElseThrow();
        assertEquals(1, reloadedBook2.getStockQuantity(), "Tồn kho Sách 2 phải giữ nguyên 1 cuốn");

        // 3. Không có đơn hàng nào được tạo
        assertEquals(0, orderRepository.count(), "Bảng orders không được lưu bất kỳ bản ghi rác nào");

        // 4. Giỏ hàng của khách không bị xóa mất
        User user = userRepository.findByUsernameOrEmail("user_rollback_test", "rollback@test.com").orElseThrow();
        assertEquals(2, cartItemRepository.findByUserId(user.getId()).size(), "Giỏ hàng phải được bảo toàn nguyên vẹn");
    }

    private void createUserIfNotExists(String username, String email) {
        if (userRepository.findByUsernameOrEmail(username, email).isEmpty()) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword("12345678");
            user.setRole("ROLE_CUSTOMER");
            userRepository.save(user);
        }
    }
}