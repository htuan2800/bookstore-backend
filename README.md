# Hệ Thống Backend Quản Lý Bán Sách Trực Tuyến (BookStore API)

> **Môn học:** Các Công nghệ Lập trình Hiện đại (CCNLTHĐ)  
> **Đề tài:** Tìm hiểu và ứng dụng Spring Boot  
> **Giảng viên hướng dẫn:** ThS. Phạm Thi Vương  
> **Trường:** Đại học Sài Gòn (SGU) — Khoa Công nghệ Thông tin  
> **Nhóm thực hiện:** Nhóm 23 (Sáng Thứ 7)  

---

## 1. Giới thiệu tổng quan
Dự án xây dựng hệ thống RESTful API phục vụ bài toán thương mại điện tử (bán sách trực tuyến) bằng **Spring Boot**. Hệ thống được thiết kế theo kiến trúc phân lớp chuẩn (*Layered Architecture*), đảm bảo tính module hóa, bảo mật và khả năng mở rộng.

### Các công nghệ và thư viện cốt lõi
* **Ngôn ngữ & Nền tảng:** Java 25 (LTS), Spring Boot 4.x
* **Cấu hình & Vòng đời:** Cấu hình chuẩn YAML (`application.yml`, `application-dev.yml`, `application-prod.yml`), Quản lý vòng đời Bean với `@PostConstruct` và `@PreDestroy`
* **Di chuyển cơ sở dữ liệu:** Flyway Migration (`V1__init_schema.sql`, `V2__seed_data.sql`)
* **Data Access & Tối ưu hóa (Tầng 3):** Spring Data JPA, Hibernate ORM, MySQL 8.0, Khóa lạc quan Optimistic Locking (`@Version`), `@EntityGraph` (chống N+1 Query), Composite Index (Chỉ mục tổ hợp), Spring Data `Pageable`
* **Bộ nhớ đệm (Caching):** Redis 8 (TTL 10 phút, Serialization JSON Jackson, Fail-safe Graceful Degradation)
* **Bảo mật:** Spring Security, JSON Web Token (Stateless JWT), Password Hashing (BCrypt)
* **Xác thực dữ liệu:** Bean Validation (`jakarta.validation`)
* **Kiểm thử tự động:** JUnit 5, Mockito, `@WebMvcTest` (Slice Test), `@SpringBootTest` (Integration Test), H2 In-Memory DB
* **Tài liệu hóa API:** SpringDoc OpenAPI 3, Swagger UI
* **DevOps & Tự động hóa:** Docker, Docker Compose, GitHub Actions (CI Pipeline)

---

## 2. Hướng dẫn cài đặt và khởi chạy dự án

### Cách 1: Khởi chạy bằng Docker Compose (Khuyến nghị — 1 lệnh duy nhất)
Yêu cầu: Máy đã cài đặt Docker và Docker Compose.

1. Khởi động toàn bộ hệ thống (gồm MySQL 8, Redis 7 và Spring Boot API):
   ```bash
   docker compose up --build -d
   ```
2. Kiểm tra container đang chạy:
   ```bash
   docker compose ps
   ```
3. Dừng hệ thống khi không sử dụng:
   ```bash
   docker compose down
   ```

*Hệ thống sẽ tự động khởi tạo database và nạp dữ liệu mẫu từ file `init.sql`.*

---

### Cách 2: Khởi chạy cục bộ (Local Development)
Yêu cầu: JDK 25+, MySQL 8.0 đang chạy ở cổng `3310` (hoặc cấu hình lại trong file `application-dev.properties`).

1. Nạp dữ liệu mẫu vào MySQL bằng cách thực thi file `init.sql`.
2. Chạy ứng dụng qua Maven Wrapper:
   * Trên Windows:
     ```cmd
     .\mvnw.cmd spring-boot:run
     ```
   * Trên Linux / macOS:
     ```bash
     ./mvnw spring-boot:run
     ```

---

## 3. Tài khoản thử nghiệm (Seed Data)
Dữ liệu mẫu đã được cấu hình sẵn trong database thông qua `init.sql`:

| Vai trò (Role) | Tên đăng nhập (Username) | Mật khẩu mặc định | Ghi chú quyền hạn |
| :--- | :--- | :--- | :--- |
| **Quản trị viên (Admin)** | `admin` | `123456` | Toàn quyền CRUD Sách, Danh mục, Tác giả, Quản lý Đơn hàng |
| **Khách hàng (Customer)** | `customer1` | `123456` | Xem sách, tìm kiếm, giỏ hàng, đặt hàng và xem đơn hàng của mình |

*Tất cả mật khẩu lưu trong cơ sở dữ liệu đều được mã hóa một chiều bằng thuật toán BCrypt.*

---

## 4. Tài liệu API (Swagger UI & OpenAPI)
Khi ứng dụng khởi động thành công tại cổng `8080`, truy cập các đường dẫn sau để xem tài liệu tương tác:

* **Giao diện Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* **OpenAPI JSON Spec:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
* **Kiểm tra sức khỏe hệ thống (Actuator Health):** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

---

## 5. Kiểm thử tự động (Automated Testing)
Dự án được trang bị bộ kiểm thử tự động toàn diện, chạy độc lập không phụ thuộc vào database bên ngoài nhờ cơ sở dữ liệu bộ nhớ ảo H2:

* **Unit Test (`OrderServiceTest`):** Kiểm thử logic nghiệp vụ đặt hàng (trừ tồn kho sách, tính tiền, xóa giỏ hàng) và các trường hợp lỗi (`InsufficientStockException`, giỏ hàng rỗng) bằng Mockito.
* **Slice Test (`BookControllerTest`):** Kiểm thử tầng Web MVC độc lập với `@WebMvcTest` và `MockMvc`, xác thực mã phản hồi HTTP (`200 OK`, `404 Not Found`) và cấu trúc JSON trả về.
* **Integration Test (`BookstoreBackendApplicationTests`):** Kiểm thử nạp ngữ cảnh ứng dụng Spring Boot (`@SpringBootTest`).

**Lệnh chạy kiểm thử:**
```bash
.\mvnw.cmd test
```

---

## 6. Cấu hình biến môi trường (`.env`)
Dự án tuân thủ nguyên tắc 12-Factor App, không lưu trữ thông tin mật trong mã nguồn:
* Môi trường dev: Sử dụng `application-dev.yml` (hoặc biến môi trường cục bộ).
* Môi trường prod (Docker): Tự động nạp từ file `.env` qua các biến:
  * `DB_URL`: Chuỗi kết nối JDBC MySQL.
  * `DB_USERNAME`: Tên đăng nhập cơ sở dữ liệu.
  * `DB_PASSWORD`: Mật khẩu cơ sở dữ liệu.
