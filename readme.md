# DDC Auction

DDC Auction là hệ thống đấu giá trực tuyến theo mô hình client-server. Server xử lý nghiệp vụ, kết nối cơ sở dữ liệu, quản lý phiên đấu giá và phát sự kiện realtime; Client là ứng dụng JavaFX cho người dùng đăng ký, đăng nhập, đăng sản phẩm, tạo phiên đấu giá, đặt giá, quản lý ví và theo dõi thông báo.

Phạm vi hệ thống tập trung vào đấu giá nhiều người dùng qua socket TCP, có giao diện desktop, lưu dữ liệu bằng MySQL và có test tự động cho các tầng DAO, service, handler, model và pattern.

## Công Nghệ Sử Dụng

- Java 25
- JavaFX 25
- Maven multi-module
- MySQL
- HikariCP
- Gson
- TCP Socket cho request-response và realtime bidding
- JUnit 5, Mockito
- JaCoCo
- GitHub Actions CI
- Cloudinary SDK cho xử lý upload ảnh

## Yêu Cầu Cài Đặt

- JDK 25, khuyến nghị Temurin 25
- Maven 3.9+
- MySQL hoặc database cloud tương thích MySQL
- IDE: VS Code hoặc IntelliJ IDEA
- Java extensions nếu dùng VS Code:
  - Extension Pack for Java
  - Test Runner for Java
  - Maven for Java

Tạo file `.env` ở thư mục gốc project:

```env
DDC_DB_URL=jdbc:mysql://<host>:<port>/<database>
DDC_DB_USER=<username>
DDC_DB_PASSWORD=<password>

DDC_SERVER_BIND_HOST=0.0.0.0
DDC_SERVER_HOST=localhost
DDC_REQUEST_PORT=8080
DDC_REALTIME_PORT=5555
DDC_IMAGE_PORT=8081
```


## Cấu Trúc Thư Mục

```text
auctionProject/
├── pom.xml                         # Parent Maven project
├── README.md
├── .env                            # Cấu hình local, không nên commit
├── .github/workflows/ci.yml        # CI build và test
├── auction-server/
│   ├── pom.xml
│   └── src/
│       ├── main/java/ddc/server/
│       │   ├── Server.java
│       │   ├── config/             # Env, database, Gson
│       │   ├── controller/         # Handler và service
│       │   ├── dao/                # Truy cập database
│       │   ├── model/              # User, item, auction, bid
│       │   ├── network/            # Socket request và realtime
│       │   ├── pattern/            # Factory, Observer, Singleton
│       │   └── security/           # Password utilities
│       └── test/java/ddc/server/   # Unit tests
└── auction-client/
    ├── pom.xml
    └── src/
        ├── main/java/ddc/client/
        │   ├── Client.java
        │   ├── controller/         # JavaFX controllers
        │   ├── model/              # DTO và view model
        │   ├── network/            # Request service và socket client
        │   └── config/             # Client config
        └── main/resources/ddc/client/
            ├── views/              # FXML screens
            ├── css/                # Styles
            └── images/             # Assets
```

## Vị Trí File JAR

// Chưa có

## Hướng Dẫn Chạy
//chưa có

Client mặc định kết nối tới:

```text
DDC_SERVER_HOST=localhost
DDC_REQUEST_PORT=8080
DDC_REALTIME_PORT=5555
```

Nếu server chạy trên máy khác, sửa `DDC_SERVER_HOST` thành ip mạng của server trong `.env`.

## Chức Năng Đã Hoàn Thành

1. Quản Lý Tài Khoản & Người Dùng (User Management)
-Xác thực: Đăng ký, đăng nhập và đổi mật khẩu bảo mật.

-Hồ sơ cá nhân: Xem và cập nhật thông tin người dùng.

-Ví điện tử tích hợp: Quản lý số dư tài khoản và hỗ trợ nạp tiền ảo để tham gia đấu giá.

2. Quản Lý Sản Phẩm Đấu Giá (Product Management)
-Phân loại đa dạng: Hỗ trợ nhiều danh mục sản phẩm khác nhau (Art, Electronics, Vehicle, General) thông qua mô hình OOP/Design Pattern linh hoạt.

-Hình ảnh sản phẩm: Tích hợp upload và hiển thị hình ảnh trực quan cho từng sản phẩm.

3. Sàn Đấu Giá Trực Tuyến (Core Auction System)
-Quản lý phiên: Tạo, xem danh sách và xem chi tiết thông tin từng phiên đấu giá.

-Đấu giá thời gian thực (Real-time Bidding): Tích hợp công nghệ Socket TCP, cho phép đặt giá, cập nhật giá hiện tại và thông tin người dẫn đầu ngay lập tức mà không cần tải lại trang.

-Cơ chế chống "bắn tỉa" (Anti-sniping): Tự động gia hạn thời gian kết thúc nếu có lượt bid sát giờ, đảm bảo tính công bằng.

-Tự động hóa: Hệ thống tự động cập nhật trạng thái phiên đấu giá (Đang diễn ra, Kết thúc) theo thời gian thực.

-Thông báo: Gửi thông báo real-time tới người dùng khi có diễn biến mới trong phiên.

4. Hệ Thống Quản Trị (Admin Dashboard)
-Quản lý người dùng: Xem danh sách, thực hiện khóa/mở khóa tài khoản vi phạm.

-Quản lý sàn đấu giá: Giám sát, kiểm tra và có quyền hủy các phiên đấu giá không hợp lệ.

-Thống kê: Xem báo cáo và số liệu thống kê tổng quan của toàn hệ thống.


## Thiết Kế Và Pattern

- Factory Method: tạo item theo category
- Fluent Setter: cấu hình item/request theo dạng chain
- Singleton: quản lý auction tập trung qua `AuctionManager`
- Observer: phát sự kiện đấu giá realtime
- DAO: tách truy cập database khỏi service
- Service layer: xử lý nghiệp vụ chính
- Handler layer: xử lý request từ client

## Giao Thức Và Kết Nối

Hệ thống dùng TCP socket vì đấu giá cần kết nối ổn định, đảm bảo thứ tự thông điệp và hạn chế mất dữ liệu khi đặt giá. Luồng chính:

- Client gửi request một lần qua port `8080` cho các chức năng thông thường.
- Client mở socket realtime qua port `5555` để subscribe auction và nhận event bid.
- Server trả response theo DTO JSON, sử dụng Gson để serialize/deserialize.
- Khi có bid mới, server cập nhật trạng thái auction và gửi event tới các client liên quan.

## Kiểm Thử

5. Kiểm Thử & Chất Lượng Code (Testing & Quality Assurance)
* Unit Testing: Bao phủ toàn diện các tầng kiến trúc trong hệ thống bao gồm DAO, Service, Handler, Model.

* Tích Hợp Liên Tục (CI/CD Pipeline)
-GitHub Actions: Tự động hóa quy trình Build và Run Test mỗi khi có code mới được push hoặc merge, đảm bảo độ ổn định của hệ thống.

Chạy toàn bộ test server:

```powershell
mvn test -B -pl auction-server
```

Chạy riêng một test class:

```powershell
mvn -pl auction-server "-Djacoco.skip=true" "-Dtest=Ten_Test" test
```

Report test sinh tại:

```text
auction-server/target/surefire-reports/
```

Coverage report sinh tại:

```text
auction-server/target/site/jacoco/
```

## Link Báo Cáo Và Demo
//Link báo cáo ở đây

//Link video ở đây

## Thành Viên Và Phân Công

- Cường:
  - Thiết kế model Item ở server
  - Áp dụng Factory Method kết hợp Fluent Setter
  - Xử lý logic và ngoại lệ item
  - Giao diện Selling và controller Selling

- Đăng:
  - Test case
  - SceneSwitcher
  - Ngoại lệ và nghiệp vụ đấu giá
  - Observer pattern cho realtime auction
  - Singleton `AuctionManager`

- Đức:
  - Thiết kế và hoàn thiện GUI
  - Nối server với database
  - Tạo và Refactoring các Model và DTO, sử dụng Fluent Setter
  - Category.java giúp tạo các phiên đấu giá có các sản phẩm thuộc các danh mục khác nhau
  - Logo DDCAuction
