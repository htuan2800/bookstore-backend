SET NAMES 'utf8mb4';
SET CHARACTER SET utf8mb4;

-- Tạo Database nếu chưa tồn tại
CREATE DATABASE IF NOT EXISTS bookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bookstore;

-- 1. Bảng Người dùng
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Mật khẩu băm BCrypt
    email VARCHAR(100) NOT NULL UNIQUE,
    full_name VARCHAR(100),
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Bảng Tác giả
CREATE TABLE IF NOT EXISTS authors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    bio TEXT
);

-- 3. Bảng Thể loại
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- 4. Bảng Sách
CREATE TABLE IF NOT EXISTS books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    description TEXT,
    image_url VARCHAR(255),
    author_id BIGINT,
    category_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (author_id) REFERENCES authors(id) ON DELETE SET NULL,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- 5. Bảng Đánh giá
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 6. Bảng Giỏ hàng
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    CONSTRAINT unique_user_book UNIQUE (user_id, book_id)
);

-- 7. Bảng Đơn hàng
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    total_amount DECIMAL(12, 2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    shipping_address TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 8. Bảng Chi tiết đơn hàng
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    book_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(12, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE RESTRICT
);

-- =========================================================
-- DỮ LIỆU MẪU (SEED DATA)
-- =========================================================

-- Nạp Tài khoản (Mật khẩu băm BCrypt đại diện cho "123456")
INSERT INTO users (username, password, email, full_name, role) VALUES
('admin', '$2a$10$Xgs.5AuzqT.psQzd0kzmI.MZwolfvEPAmlhHp3a3d5N7dHO42QGW.', 'admin@bookstore.com', 'Quản Trị Viên', 'ROLE_ADMIN'),
('customer1', '$2a$10$Xgs.5AuzqT.psQzd0kzmI.MZwolfvEPAmlhHp3a3d5N7dHO42QGW.', 'user1@gmail.com', 'Nguyễn Văn A', 'ROLE_CUSTOMER');

-- Nạp Tác giả
INSERT INTO authors (name, bio) VALUES
('Tô Hoài', 'Nhà văn nổi tiếng với tác phẩm Dế Mèn Phiêu Lưu Ký'),
('J.K. Rowling', 'Tác giả bộ truyện Harry Potter lừng danh thế giới');

-- Nạp Thể loại
INSERT INTO categories (name, description) VALUES
('Văn học Việt Nam', 'Các tác phẩm văn học trong nước'),
('Viễn tưởng', 'Sách khoa học viễn tưởng và phép thuật');

-- Nạp Sách
INSERT INTO books (title, price, stock_quantity, description, author_id, category_id) VALUES
('Dế Mèn Phiêu Lưu Ký', 50000.00, 100, 'Truyện đồng thoại dành cho thiếu nhi', 1, 1),
('Harry Potter và Hòn Đá Phù Thủy', 150000.00, 50, 'Tập 1 bộ truyện Harry Potter', 2, 2);