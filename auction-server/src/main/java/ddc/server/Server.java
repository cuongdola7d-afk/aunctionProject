package ddc.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ddc.server.controller.AuctionController;
import ddc.server.model.item.Item;
import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.AuctionStatus;
import ddc.server.model.user.Bidder;
import ddc.server.network.AuctionEventDispatcher;
import ddc.server.network.ClientHandler;
import ddc.server.pattern.observer.AuctionEvent;
import ddc.server.pattern.observer.AuctionEventType;
import ddc.server.service.AuctionService;

public class Server {
    private static final int PORT = 5555;

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<String, Auction> auctionStore = new ConcurrentHashMap<>();
    private final Map<String, Bidder> bidderStore = new ConcurrentHashMap<>();

    private final AuctionService auctionService;
    private final AuctionController auctionController;
    private final AuctionEventDispatcher dispatcher;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    public Server() throws Exception {
        this.auctionService = new AuctionService();
        this.auctionController = new AuctionController(auctionService);
        this.dispatcher = new AuctionEventDispatcher();

        auctionService.addObserver(dispatcher);

        seedDemoData();
    }

    public void start() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Auction server running on port " + PORT);
            printDemoIds();

            while (true) {
                Socket socket = serverSocket.accept();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                ClientHandler handler = new ClientHandler(
                        socket,
                        reader,
                        writer,
                        auctionController,
                        dispatcher,
                        auctionStore,
                        bidderStore
                );

                executor.submit(handler);
            }
        }
    }

    private void seedDemoData() throws Exception {
        Item item = new DemoItem("Ban phim co", "Demo keyboard", 100000);

        Auction auction = auctionService.createAuction(
                item,
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().plusMinutes(30)
        );

        Bidder alice = new Bidder();
        alice.setName("Alice");

        Bidder bob = new Bidder();
        bob.setName("Bob");

        Bidder charlie = new Bidder();
        charlie.setName("Charlie");

        auctionStore.put(auction.getId(), auction);
        bidderStore.put(alice.getId(), alice);
        bidderStore.put(bob.getId(), bob);
        bidderStore.put(charlie.getId(), charlie);
    }

    private void printDemoIds() {
        System.out.println("=== DEMO AUCTIONS ===");
        for (Auction auction : auctionStore.values()) {
            System.out.println("auctionId = " + auction.getId()
                    + " | item = " + (auction.getItem() != null ? auction.getItem().getName() : "null"));
        }

        System.out.println("=== DEMO BIDDERS ===");
        for (Bidder bidder : bidderStore.values()) {
            System.out.println("bidderId = " + bidder.getId()
                    + " | name = " + bidder.getName());
        }
    }

    private void startAutoSchedule() {
        scheduler.scheduleAtFixedRate(() -> {
            LocalDateTime now = LocalDateTime.now();
            
            // Duyệt qua tất cả phiên đấu giá trong kho
            auctionStore.values().forEach(auction -> {
                // Nếu phiên đang chạy mà đã quá giờ kết thúc
                if (auction.getStatus() == AuctionStatus.RUNNING && 
                    auction.getEndTime().isBefore(now)) {
                    
                    processAuctionEnd(auction);
                }
            });
        }, 0, 1, TimeUnit.SECONDS); // Chạy ngay lập tức, lặp lại mỗi 1 giây
    }

    private void processAuctionEnd(Auction auction) {
    // 1. Cập nhật trạng thái
    auction.setStatus(AuctionStatus.FINISHED);

    // 2. Lấy tên người thắng an toàn (tránh lỗi nếu không có ai bid)
    String winnerName = (auction.getHighestBidder() != null) 
                        ? auction.getHighestBidder().getName() 
                        : "Không có";

    // 3. Tạo sự kiện với ĐẦY ĐỦ 10 tham số
    AuctionEvent endEvent = new AuctionEvent(
        AuctionEventType.AUCTION_FINISHED, // 1. type
        auction.getId(),                   // 2. auctionId
        auction.getItem().getId(),         // 3. itemId
        auction.getItem().getName(),       // 4. itemName
        winnerName,                        // 5. bidderName
        auction.getCurrentPrice(),         // 6. bidAmount
        auction.getCurrentPrice(),         // 7. currentPrice
        AuctionStatus.FINISHED,            // 8. status
        LocalDateTime.now(),               // 9. eventTime
        "Phiên đấu giá đã kết thúc!"       // 10. message
    );

    // 4. Bắn tin
    dispatcher.dispatch(endEvent);
}

    public static void main(String[] args) throws Exception {
        new Server().start();
    }

    private static class DemoItem extends Item {
        public DemoItem(String name, String description, double startingPrice) {
            super(name, description, startingPrice);
            setCurrentPrice(startingPrice);
        }

        @Override
        public String getCategory() {
            return "DEMO";
        }
    }
}