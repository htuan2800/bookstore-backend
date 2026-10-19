-- V2__seed_data.sql: Nạp dữ liệu mẫu ban đầu (Flyway Migration)

-- 1. Nạp Tài khoản (Mật khẩu băm BCrypt của "123456")
INSERT INTO users (id, username, password, email, full_name, role) VALUES
(1, 'admin', '$2a$10$G7H1CaSaBN99h3cw6CHK0OX6.08Q19KdXXGoP5gv7nTggkHHiOmSS', 'admin@bookstore.com', 'Quản Trị Viên', 'ROLE_ADMIN'),
(2, 'customer1', '$2a$10$G7H1CaSaBN99h3cw6CHK0OX6.08Q19KdXXGoP5gv7nTggkHHiOmSS', 'user1@gmail.com', 'Nguyễn Văn A', 'ROLE_CUSTOMER')
ON DUPLICATE KEY UPDATE username=username;

-- 2. Nạp Tác giả
INSERT INTO authors (id, name, bio) VALUES
(1, 'Tô Hoài', 'Nhà văn nổi tiếng với tác phẩm Dế Mèn Phiêu Lưu Ký'),
(2, 'J.K. Rowling', 'Tác giả bộ truyện Harry Potter lừng danh thế giới')
ON DUPLICATE KEY UPDATE name=name;

-- 3. Nạp Thể loại
INSERT INTO categories (id, name, description) VALUES
(1, 'Văn học Việt Nam', 'Các tác phẩm văn học trong nước'),
(2, 'Viễn tưởng', 'Sách khoa học viễn tưởng và phép thuật')
ON DUPLICATE KEY UPDATE name=name;

-- 4. Nạp Sách
INSERT INTO books (id, title, price, stock_quantity, description, author_id, category_id, version)
WITH RECURSIVE book_seq AS (
    SELECT 3 AS n
    UNION ALL
    SELECT n + 1 FROM book_seq WHERE n <= 102
)
SELECT 
    n AS id,
    CONCAT('Sách Kỹ Thuật & Văn Học Tập ', n) AS title,
    ROUND(50000 + (n * 1500), -3) AS price,
    (20 + (n % 80)) AS stock_quantity,
    CONCAT('Mô tả chi tiết nội dung cuốn sách mẫu số ', n, ' dùng cho kiểm thử hiệu năng.') AS description,
    IF(n % 2 = 0, 2, 1) AS author_id,
    IF(n % 2 = 0, 2, 1) AS category_id,
    0 AS version
FROM book_seq
ON DUPLICATE KEY UPDATE title = title;