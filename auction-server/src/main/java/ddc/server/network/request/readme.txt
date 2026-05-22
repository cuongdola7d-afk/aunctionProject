===============================================================
         NETWORK/REQUEST - DTO CHO KÊNH REALTIME (PORT 5555)
===============================================================

1. MỤC ĐÍCH
   - Chứa các DTO (Data Transfer Object) dùng để parse/tạo JSON
     trên kênh realtime giữa client và server.
   - Kênh realtime dùng cho bidding trực tiếp, khác với kênh
     request-response (port 8080) dùng cho login, register, add item...

2. CÁC FILE

   ┌─────────────────────────────────────────────────────────────┐
   │  PlaceBidRequest.java            [Client → Server]          │
   │  - auctionId  : ID phiên đấu giá                           │
   │  - bidderId   : ID người đặt giá                            │
   │  - amount     : số tiền đặt                                 │
   │  → Dùng khi client gửi lệnh đặt giá (PLACE_BID)            │
   └─────────────────────────────────────────────────────────────┘

   ┌─────────────────────────────────────────────────────────────┐
   │  SubscribeAuctionRequest.java    [Client → Server]          │
   │  - auctionId  : ID phiên đấu giá muốn theo dõi             │
   │  → Dùng khi client muốn subscribe để nhận event realtime    │
   └─────────────────────────────────────────────────────────────┘

   ┌─────────────────────────────────────────────────────────────┐
   │  AuctionEventPayload.java        [Server → Client]          │
   │  - eventType      : "SNAPSHOT" hoặc "NEW_BID"               │
   │  - auctionId      : ID phiên đấu giá                       │
   │  - currentPrice   : giá hiện tại                            │
   │  - status         : trạng thái phiên (RUNNING, FINISHED...) │
   │  - bidderName     : tên người bid cao nhất / vừa bid        │
   │  - bidAmount      : số tiền bid (dùng cho NEW_BID)          │
   │  - startTime      : thời gian bắt đầu                       │
   │  - endTime        : thời gian kết thúc                       │
   │  - timeExtended   : true nếu bị gia hạn bởi anti-snip      │
   │  - minBidIncrement: bước giá tối thiểu                      │
   │  - message        : thông báo kèm theo                      │
   │  → Server gửi ngược cho client khi có thay đổi trong phiên  │
   └─────────────────────────────────────────────────────────────┘

3. LUỒNG HOẠT ĐỘNG

   === Subscribe (theo dõi phiên đấu giá) ===

   Client mở trang chi tiết auction
       │
       │  Gửi JSON: { action: "SUBSCRIBE", auctionId: "abc123" }
       ▼
   RealtimeClientHandler nhận request
       → parse thành SubscribeAuctionRequest
       → đăng ký client vào danh sách theo dõi auction "abc123"
       │
       │  Gửi ngược SNAPSHOT (trạng thái hiện tại)
       ▼
   Client nhận AuctionEventPayload { eventType: "SNAPSHOT", ... }
       → cập nhật UI: giá, trạng thái, thời gian...

   === Place Bid (đặt giá) ===

   Client nhấn nút đặt giá
       │
       │  Gửi JSON: { action: "PLACE_BID",
       │              auctionId: "abc123",
       │              bidderId: "user456",
       │              amount: 500000 }
       ▼
   RealtimeClientHandler nhận request
       → parse thành PlaceBidRequest
       → gọi AuctionService.placeBid(auction, bidder, amount, time)
       → validate (giá hợp lệ, trạng thái RUNNING, bước giá, ...)
       │
       │  Nếu thành công:
       ▼
   Auction.notifyObservers(event)
       → LoggingAuctionObserver broadcast tới TẤT CẢ client
         đang subscribe auction "abc123"
       │
       ▼
   Mỗi client nhận AuctionEventPayload:
   {
       eventType: "NEW_BID",
       auctionId: "abc123",
       currentPrice: 500000,
       bidderName: "user456",
       bidAmount: 500000,
       timeExtended: false,
       minBidIncrement: 50000,
       ...
   }
       → cập nhật UI realtime

4. SƠ ĐỒ TỔNG QUAN

   Client A (đặt bid)              Client B (đang xem)
       │                                │
       │ PlaceBidRequest                 │ SubscribeAuctionRequest
       ▼                                ▼
   ┌────────────────────────────────────────┐
   │         RealtimeClientHandler          │
   │         (port 5555)                    │
   │                                        │
   │  parse JSON → DTO → AuctionService     │
   │                        │               │
   │                        ▼               │
   │                 AuctionManager         │
   │                 (lấy Auction/Bidder)   │
   │                        │               │
   │                        ▼               │
   │               Auction.placeBid()       │
   │               notifyObservers()        │
   │                        │               │
   │                        ▼               │
   │           LoggingAuctionObserver       │
   │           broadcast AuctionEventPayload│
   └───────────┬───────────────────┬────────┘
               │                   │
               ▼                   ▼
          Client A             Client B
          (nhận event)         (nhận event)
          UI cập nhật          UI cập nhật

5. LƯU Ý
   - PlaceBidRequest và SubscribeAuctionRequest là DTO chiều
     Client → Server (nhận vào).
   - AuctionEventPayload là DTO chiều Server → Client (gửi ra).
   - Cả 3 đều chỉ dùng cho kênh REALTIME (port 5555),
     không liên quan đến kênh request-response (port 8080).
