===============================================================
              OBSERVER PATTERN - HỆ THỐNG ĐẤU GIÁ
===============================================================

1. MỤC ĐÍCH
   - Khi phiên đấu giá có thay đổi (bid mới, bắt đầu, kết thúc...),
     server tự động thông báo tới tất cả client đang theo dõi phiên đó.
   - Tách biệt logic "phát sinh sự kiện" (Auction) và "xử lý sự kiện"
     (log, broadcast) để dễ mở rộng.

2. CÁC THÀNH PHẦN

   ┌─────────────────────────────────────────────────────────────┐
   │  AuctionSubject (interface)                                 │
   │  - addObserver(AuctionObserver)                             │
   │  - removeObserver(AuctionObserver)                          │
   │  - notifyObservers(AuctionEvent)                            │
   │  → Được implement bởi: model Auction                       │
   └─────────────────────────────────────────────────────────────┘
                          │
                          │ gọi update() cho từng observer
                          ▼
   ┌─────────────────────────────────────────────────────────────┐
   │  AuctionObserver (interface)                                │
   │  - update(AuctionEvent)                                     │
   │  → Được implement bởi: LoggingAuctionObserver               │
   └─────────────────────────────────────────────────────────────┘
                          │
                          │ dùng dữ liệu từ
                          ▼
   ┌─────────────────────────────────────────────────────────────┐
   │  AuctionEvent (data class)                                  │
   │  - auctionId, itemId, itemName                              │
   │  - bidderName, bidAmount, currentPrice                      │
   │  - status (AuctionStatus)                                   │
   │  - eventTime (LocalDateTime)                                │
   │  - message                                                  │
   └─────────────────────────────────────────────────────────────┘
                          │
                          │ loại sự kiện được phân loại bởi
                          ▼
   ┌─────────────────────────────────────────────────────────────┐
   │  AuctionEventType (enum)                                    │
   │  - NEW_BID           : có bid mới                           │
   │  - AUCTION_STARTED   : phiên bắt đầu                       │
   │  - AUCTION_FINISHED  : phiên kết thúc                       │
   │  - AUCTION_CANCELLED : phiên bị hủy                         │
   │  - STATUS_CHANGED    : trạng thái thay đổi                  │
   └─────────────────────────────────────────────────────────────┘

3. LUỒNG HOẠT ĐỘNG CHI TIẾT

   Ví dụ: Bidder đặt giá mới

   Bước 1: Client gửi lệnh PLACE_BID qua socket realtime (port 5555)
           │
           ▼
   Bước 2: RealtimeClientHandler nhận request
           → gọi AuctionService.placeBid(auction, bidder, amount, time)
           │
           ▼
   Bước 3: AuctionService validate (giá hợp lệ, trạng thái RUNNING, ...)
           → gọi auction.placeBid(bid)
           │
           ▼
   Bước 4: Auction (Subject) cập nhật state
           → tạo AuctionEvent với type = NEW_BID
           → gọi notifyObservers(event)
           │
           ▼
   Bước 5: notifyObservers() duyệt danh sách observer
           → gọi observer.update(event) cho từng observer đã đăng ký
           │
           ▼
   Bước 6: LoggingAuctionObserver.update(event) thực hiện:
           │
           ├── 6a. Log ra console (LOGGER.info)
           │       "[AuctionObserver] type=NEW_BID, auctionId=xxx, ..."
           │
           └── 6b. Broadcast tới client:
                   - Duyệt Set<ClientConnection> activeConnections
                   - Chỉ gửi cho client đang subscribe đúng auctionId
                   - Chuyển AuctionEvent → JsonObject (toClientPayload)
                   - Gửi qua connection.send(AUCTION_EVENT, payload)
                     │
                     ▼
   Bước 7: Client nhận JSON qua socket
           → parse AuctionEventResponse
           → gọi ServerMessageListener.onAuctionEvent()
           → Controller cập nhật UI (giá mới, bidder mới, ...)

4. SƠ ĐỒ TỔNG QUAN

   Client A (đặt bid)
       │
       │  PLACE_BID request
       ▼
   ┌──────────────────┐     ┌──────────────────┐
   │ RealtimeClient   │────▶│  AuctionService   │
   │ Handler          │     │  (validate+logic) │
   └──────────────────┘     └────────┬──────────┘
                                     │
                                     ▼
                            ┌──────────────────┐
                            │  Auction (Subject)│
                            │  placeBid()       │
                            │  notifyObservers()│
                            └────────┬──────────┘
                                     │
                                     ▼
                            ┌──────────────────────────┐
                            │ LoggingAuctionObserver    │
                            │ update(event)             │
                            │   ├─ log to console       │
                            │   └─ broadcast to clients │
                            └────────┬─────────────────┘
                                     │
                      ┌──────────────┼──────────────┐
                      ▼              ▼              ▼
                 Client A       Client B       Client C
                 (người bid)   (đang xem)     (đang xem)
                 → UI cập nhật  → UI cập nhật  → UI cập nhật

5. CÁCH MỞ RỘNG
   - Muốn thêm tính năng mới (gửi email, lưu log DB, push notification)?
     → Tạo class mới implement AuctionObserver
     → Đăng ký vào Auction bằng addObserver()
     → Không cần sửa code Auction hay LoggingAuctionObserver
   - Đây là ưu điểm chính của Observer Pattern: Open/Closed Principle.