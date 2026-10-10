-- V2__seed_data.sql: Nạp dữ liệu mẫu ban đầu (Flyway Migration)

-- 1. Nạp Tài khoản (Mật khẩu băm BCrypt của "123456")
INSERT INTO users (id, username, password, email, full_name, role) VALUES
(1, 'admin', '$2a$10$Xgs.5AuzqT.psQzd0kzmI.MZwolfvEPAmlhHp3a3d5N7dHO42QGW.', 'admin@bookstore.com', 'Quản Trị Viên', 'ROLE_ADMIN'),
(2, 'customer1', '$2a$10$Xgs.5AuzqT.psQzd0kzmI.MZwolfvEPAmlhHp3a3d5N7dHO42QGW.', 'user1@gmail.com', 'Nguyễn Văn A', 'ROLE_CUSTOMER')
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
INSERT INTO books (id, title, price, stock_quantity, description, author_id, category_id, version) VALUES
(1, 'Dế Mèn Phiêu Lưu Ký', 50000.00, 100, 'Truyện đồng thoại kinh điển dành cho thiếu nhi', 1, 1, 0),
(2, 'Harry Potter và Hòn Đá Phù Thủy', 150000.00, 50, 'Tập 1 bộ truyện phép thuật Harry Potter', 2, 2, 0)
ON DUPLICATE KEY UPDATE title=title;
