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

Chạy từ thư mục gốc:

```powershell
cd "D:\File Jva\AuctionProjectVScode\auctionProject"
```

### 1. Build project

```powershell
mvn clean compile
```

### 2. Chạy test server

```powershell
mvn test -B -pl auction-server
```

Nếu chạy local bằng JDK mới và JaCoCo sinh log dài, có thể tắt JaCoCo:

```powershell
mvn test -B -pl auction-server "-Djacoco.skip=true"
```

### 3. Chạy Server trước

Chạy bằng IDE:

- Mở `auction-server/src/main/java/ddc/server/Server.java`
- Chọn `Run Java`

Hoặc chạy bằng Maven:

```powershell
mvn -pl auction-server exec:java "-Dexec.mainClass=ddc.server.Server"
```

Server mở các cổng mặc định:

- `8080`: request-response cho login, register, add item, lấy dữ liệu
- `5555`: realtime socket cho subscribe auction và bidding
- `8081`: image/static resource port nếu được dùng

### 4. Chạy Client sau

Chạy bằng IDE:

- Mở `auction-client/src/main/java/ddc/client/Client.java`
- Chọn `Run Java`

Hoặc chạy bằng JavaFX Maven plugin:

```powershell
mvn -pl auction-client javafx:run
```

Client mặc định kết nối tới:

```text
DDC_SERVER_HOST=localhost
DDC_REQUEST_PORT=8080
DDC_REALTIME_PORT=5555
```

Nếu server chạy trên máy khác, sửa `DDC_SERVER_HOST` trong `.env`.

## Chức Năng Đã Hoàn Thành

- Đăng ký tài khoản người dùng
- Đăng nhập người dùng
- Cập nhật thông tin cá nhân
- Đổi mật khẩu
- Ví người dùng và nạp tiền
- Thêm sản phẩm đấu giá
- Hỗ trợ nhiều loại item: Art, Electronics, Vehicle, General
- Upload và hiển thị ảnh sản phẩm
- Tạo phiên đấu giá
- Xem danh sách phiên đấu giá
- Xem chi tiết phiên đấu giá
- Đặt giá trong phiên đấu giá
- Kiểm tra giá hiện tại và người đặt giá cao nhất
- Realtime bidding qua socket TCP
- Tự động cập nhật trạng thái phiên đấu giá theo thời gian
- Cơ chế chống bid sát giờ bằng gia hạn thời gian kết thúc
- Thông báo cho người dùng
- Trang quản trị admin
- Admin xem danh sách người dùng
- Admin khóa/mở trạng thái người dùng
- Admin xem thống kê hệ thống
- Admin xem và hủy phiên đấu giá
- Unit test cho DAO, service, handler, model và design pattern
- CI build và test bằng GitHub Actions

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

Chạy toàn bộ test server:

```powershell
mvn test -B -pl auction-server
```

Chạy riêng một test class:

```powershell
mvn -pl auction-server "-Djacoco.skip=true" "-Dtest=ItemDAOTest" test
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
  - Socket kết nối client-server và server-database
  - Giao diện đăng nhập, đăng ký
  - Xử lý dữ liệu đăng nhập, đăng ký
  - Model user
  - Quản lý database
