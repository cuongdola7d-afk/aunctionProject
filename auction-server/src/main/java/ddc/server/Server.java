package ddc.server;

// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import ddc.server.network.ClientHandler;
// import ddc.server.pattern.observer.AuctionEvent;
// import ddc.server.pattern.observer.AuctionEventType;
// import ddc.server.service.AuctionService;

public class Server {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server opened!");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New Client Detected! - " + clientSocket.getInetAddress());
                
                ClientHandler handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    // ID cố định để test
//     public static final String DEMO_AUCTION_ID = "AUCT-001";
//     public static final String BIDDER_ALICE_ID = "BIDDER-ALICE";
//     public static final String BIDDER_BOB_ID = "BIDDER-BOB";
//     public static final String BIDDER_CHARLIE_ID = "BIDDER-CHARLIE";

//     private final ExecutorService executor = Executors.newCachedThreadPool();
//     private final Map<String, Auction> auctionStore = new ConcurrentHashMap<>();
//     private final Map<String, Bidder> bidderStore = new ConcurrentHashMap<>();

//     private final AuctionService auctionService;
//     private final AuctionController auctionController;
//     private final AuctionEventDispatcher dispatcher;

//     private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
//     public Server() throws Exception {
//         this.auctionService = new AuctionService();
//         this.auctionController = new AuctionController(auctionService);
//         this.dispatcher = new AuctionEventDispatcher();

//         auctionService.addObserver(dispatcher);

//         seedDemoData();
//     }

//     // public void start() throws Exception {
//     //     try (ServerSocket serverSocket = new ServerSocket(PORT)) {
//     //         System.out.println("Auction server running on port " + PORT);
//     //         printDemoIds();
//     //         //Khởi động luồng chạy status liên tục
//     //         startAutoSchedule();
//     //         startStatusMonitor();
//     //         while (true) {
//     //             Socket socket = serverSocket.accept();

//     //             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//     //             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

//     //             ClientHandler handler = new ClientHandler(
//     //                     socket,
//     //                     reader,
//     //                     writer,
//     //                     auctionController,
//     //                     dispatcher
//     //             );

//     //             executor.submit(handler);
//     //         }
//     //     }
//     // }

//     private void startAutoSchedule() {
//         scheduler.scheduleAtFixedRate(() -> {
//             try{
//             LocalDateTime now = LocalDateTime.now();
//             System.out.println("DEBUG: Đang quét " + auctionStore.size());
//             // Duyệt qua tất cả phiên đấu giá trong kho
//             auctionStore.values().forEach(auction -> {
//                 if (auction.getStatus() == AuctionStatus.OPEN && !auction.getStartTime().isAfter(now)) {
//                 processAuctionStart(auction);
//                 }
//                 // Nếu phiên đang chạy mà đã quá giờ kết thúc
//                 if (auction.getStatus() == AuctionStatus.RUNNING && 
//                     auction.getEndTime().isBefore(now)) {
                    
//                     processAuctionEnd(auction);
//                 }
//             });
//         }
//         catch (Exception e) {
//         e.printStackTrace(); // In lỗi ra để biết tại sao nó chết
//     }
//         }, 0, 1, TimeUnit.SECONDS); // Chạy ngay lập tức, lặp lại mỗi 1 giây
//         System.out.println(">>> Hệ thống quét tự động đã kích hoạt.");
//     }

    
//     private void processAuctionStart(Auction auction) {
//         // 1. Cập nhật trạng thái trong đối tượng Auction trên RAM
//         auction.setStatus(AuctionStatus.RUNNING);

//         // 2. Log ra Terminal để bạn test (Check status chuyển từ OPEN -> RUNNING)
//         System.out.println("[KÍCH HOẠT] Phiên đấu giá đã chính thức bắt đầu!");
    

//         AuctionEvent startEvent = new AuctionEvent(
//             AuctionEventType.AUCTION_STARTED,      // 1. type
//             auction.getId(),                        // 2. auctionId
//             auction.getItem().getId(),              // 3. itemId
//             auction.getItem().getName(),            // 4. itemName
//             "Hệ thống",                             // 5. bidderName (Chưa có ai bid)
//             auction.getItem().getStartingPrice(),   // 6. bidAmount
//             auction.getItem().getStartingPrice(),   // 7. currentPrice
//             AuctionStatus.RUNNING,                  // 8. status
//             LocalDateTime.now(),                    // 9. eventTime
//             "Phiên đấu giá đã bắt đầu! Mời các bạn đặt giá." // 10. message
//         );

//         // 4. Gửi sự kiện này tới Dispatcher để báo cho các Client đang hóng
//         if (dispatcher != null) {
//             dispatcher.dispatch(startEvent);
//         }
//     }

//     private void processAuctionEnd(Auction auction) {
//         // 1. Cập nhật trạng thái
//         auction.setStatus(AuctionStatus.FINISHED);

//         // 2. Lấy tên người thắng an toàn (tránh lỗi nếu không có ai bid)
//         String winnerName = (auction.getHighestBidder() != null) 
//                             ? auction.getHighestBidder().getName() 
//                             : "Không có";

//         // 3. Tạo sự kiện với ĐẦY ĐỦ 10 tham số
//         AuctionEvent endEvent = new AuctionEvent(
//             AuctionEventType.AUCTION_FINISHED, // 1. type
//             auction.getId(),                   // 2. auctionId
//             auction.getItem().getId(),         // 3. itemId
//             auction.getItem().getName(),       // 4. itemName
//             winnerName,                        // 5. bidderName
//             auction.getCurrentPrice(),         // 6. bidAmount
//             auction.getCurrentPrice(),         // 7. currentPrice
//             AuctionStatus.FINISHED,            // 8. status
//             LocalDateTime.now(),               // 9. eventTime
//             "Phiên đấu giá đã kết thúc!"       // 10. message
//         );

//         // 4. Bắn tin
//         dispatcher.dispatch(endEvent);
//         System.out.println("\n[THÔNG BÁO] Phát hiện phiên kết thúc!");
//         System.out.println("Trạng thái mới: " + auction.getStatus()); // Nó sẽ in FINISHED ở đây
//         System.out.println("--------------------------------------");
//     }

//     private void processAuctionCancel(Auction auction) {
//         // 1. Đổi trạng thái sang CANCELLED
//         auction.cancelAuction();

//         // 2. Tạo sự kiện để báo cho Client
//         AuctionEvent cancelEvent = new AuctionEvent(
//             AuctionEventType.AUCTION_CANCELLED,
//             auction.getId(),
//             auction.getItem().getId(),
//             auction.getItem().getName(),
//             "Hệ thống",
//             0,
//             auction.getCurrentPrice(),
//             AuctionStatus.CANCELLED,
//             LocalDateTime.now(),
//             "THÔNG BÁO: Phiên đấu giá này đã bị hủy bởi quản trị viên!"
//         );

//         // 3. Bắn tin cho tất cả Client đang kết nối
//         if (dispatcher != null) {
//             dispatcher.dispatch(cancelEvent);
//         }
//         System.out.println(">>> ĐÃ HỦY PHIÊN: " + auction.getItem().getName());
//     }
    
// // Tạo luồng test Status thay đổi
//     private void startStatusMonitor() {
//         new Thread(() -> {
//             while (true) {
//                 try {
//                     Thread.sleep(1000); // 10 giây in một lần
//                     System.out.println("\n===== BANG TRANG THAI HIEN TAI =====");
//                     auctionStore.values().forEach(a -> {
//                         System.out.printf("ID: %s | Item: %s | Status: %s\n", 
//                             a.getId().substring(0, 8), a.getItem().getName(), a.getStatus());
//                     });
//                     System.out.println("====================================\n");
//                 } catch (InterruptedException e) {
//                     e.printStackTrace();
//                 }
//             }
//         }).start();
//     }
//     private void seedDemoData() throws Exception {
//         Item item = new DemoItem("Ban phim co", "Demo keyboard", 100000);

//         Auction auction = auctionService.createAuction(
//                 item,
//                 LocalDateTime.now().minusSeconds(1),
//                 LocalDateTime.now().plusSeconds(10)
//         );
//         auction.setId(DEMO_AUCTION_ID);

//         // Đưa vào kho lưu trữ của Server để Scheduler nhìn thấy
//         auctionStore.put(auction.getId(), auction); 
        
//         // Đặt trạng thái ban đầu là OPEN để nó có thể chuyển sang RUNNING
//         auction.setStatus(AuctionStatus.OPEN);

//         Bidder alice = new Bidder();
//         alice.setId(BIDDER_ALICE_ID);
//         alice.setName("Alice");

//         Bidder bob = new Bidder();
//         bob.setId(BIDDER_BOB_ID);
//         bob.setName("Bob");

//         Bidder charlie = new Bidder();
//         charlie.setId(BIDDER_CHARLIE_ID);
//         charlie.setName("Charlie");

//         auctionStore.clear();
//         bidderStore.clear();

//         auctionStore.put(auction.getId(), auction);
//         bidderStore.put(alice.getId(), alice);
//         bidderStore.put(bob.getId(), bob);
//         bidderStore.put(charlie.getId(), charlie);
//     }

//     private void printDemoIds() {
//         System.out.println("=== DEMO AUCTIONS ===");
//         System.out.println("auctionId = " + DEMO_AUCTION_ID + " | item = Ban phim co");

//         System.out.println("=== DEMO BIDDERS ===");
//         System.out.println("bidderId = " + BIDDER_ALICE_ID + " | name = Alice");
//         System.out.println("bidderId = " + BIDDER_BOB_ID + " | name = Bob");
//         System.out.println("bidderId = " + BIDDER_CHARLIE_ID + " | name = Charlie");
//     }


//     // public static void main(String[] args) throws Exception {
//     //     new Server().start();
//     // }

//     private static class DemoItem extends Item {
//         public DemoItem(String name, String description, double startingPrice) {
//             super(name, description, startingPrice);
//             setCurrentPrice(startingPrice);
//         }

//         @Override
//         public String getCategory() {
//             return "DEMO";
//         }
//     }
}