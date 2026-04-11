package ddc.server.network;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

import com.google.gson.Gson;

import ddc.server.controller.AuctionController;
import ddc.server.model.transaction.Auction;
import ddc.server.model.user.Bidder;

public class ClientHandler implements Runnable {
    private final ClientConnection connection;
    private final AuctionController auctionController;
    private final AuctionEventDispatcher dispatcher;
    private final Map<String, Auction> auctionStore;
    private final Map<String, Bidder> bidderStore;
    private final Gson gson = new Gson();

    public ClientHandler(
            Socket socket,
            BufferedReader reader,
            PrintWriter writer,
            AuctionController auctionController,
            AuctionEventDispatcher dispatcher,
            Map<String, Auction> auctionStore,
            Map<String, Bidder> bidderStore
    ) {
        this.connection = new ClientConnection(socket, reader, writer);
        this.auctionController = auctionController;
        this.dispatcher = dispatcher;
        this.auctionStore = auctionStore;
        this.bidderStore = bidderStore;
    }

    @Override
    public void run() {
        try {
            String line;

            while ((line = connection.getReader().readLine()) != null) {
                handleRawMessage(line);
            }
        } catch (Exception e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            dispatcher.unsubscribeAll(connection);
            connection.close();
        }
    }

    private void handleRawMessage(String line) {
        try {
            SocketMessage message = gson.fromJson(line, SocketMessage.class);

            if (message == null || message.getType() == null) {
                sendError("Invalid message.");
                return;
            }

            switch (message.getType()) {
                case SUBSCRIBE_AUCTION -> handleSubscribe(message.getPayloadJson());
                case PLACE_BID -> handlePlaceBid(message.getPayloadJson());
                default -> sendError("Unsupported message type: " + message.getType());
            }
        } catch (Exception e) {
            sendError("Failed to parse message: " + e.getMessage());
        }
    }

private void handleSubscribe(String payloadJson) {
    SubscribeAuctionRequest request = gson.fromJson(payloadJson, SubscribeAuctionRequest.class);

    if (request == null || request.getAuctionId() == null || request.getAuctionId().isBlank()) {
        sendError("auctionId is required.");
        return;
    }

    System.out.println("=== DEBUG SUBSCRIBE ===");
    System.out.println("request auctionId = [" + request.getAuctionId() + "]");
    System.out.println("request length    = " + request.getAuctionId().length());
    System.out.println("available auction ids:");
    for (String id : auctionStore.keySet()) {
        System.out.println("[" + id + "] len=" + id.length());
    }
    System.out.println("containsKey = " + auctionStore.containsKey(request.getAuctionId()));

    Auction auction = auctionStore.get(request.getAuctionId());
    if (auction == null) {
        sendError("Auction not found: " + request.getAuctionId());
        return;
    }

    auctionController.handleRefreshStatus(auction);
    dispatcher.subscribe(request.getAuctionId(), connection);

    AuctionEventResponse snapshot = AuctionEventResponse.fromAuctionState(auction);
    connection.send(MessageType.AUCTION_EVENT, snapshot, gson);
}
     
    

    private void handlePlaceBid(String payloadJson) {
        PlaceBidRequest request = gson.fromJson(payloadJson, PlaceBidRequest.class);

        if (request == null) {
            sendError("Invalid place bid request.");
            return;
        }

        Auction auction = auctionStore.get(request.getAuctionId());
        if (auction == null) {
            sendError("Auction not found: " + request.getAuctionId());
            return;
        }

        Bidder bidder = bidderStore.get(request.getBidderId());
        if (bidder == null) {
            sendError("Bidder not found: " + request.getBidderId());
            return;
        }

        try {
            auctionController.handlePlaceBid(auction, bidder, request.getAmount());
        } catch (Exception e) {
            sendError(e.getMessage());
        }
    }

    private void sendError(String message) {
        connection.send(MessageType.ERROR, new ErrorResponse(message), gson);
    }
}
// public class ClientHandler implements Runnable {
//     private Socket socket; // Đường dây liên lạc riêng với 1 khách
//     private AuctionServer server; // Để báo cáo lại cho Server

//     public ClientHandler(Socket socket, AuctionServer server) {
//         this.socket = socket;
//         this.server = server;
//     }

//     @Override
//     public void run() {
//         try {
//             // 1. Tạo bộ đọc và bộ ghi dữ liệu qua Socket
//             // 2. Vòng lặp while(true) để đợi lệnh từ Client
//             // 3. Nếu Client gửi lệnh BID -> Xử lý và gọi server.broadcast()
//         } catch (IOException e) {
//             // Xử lý khi khách hàng ngắt kết nối (tắt app)
//         }
//     }

//     public void sendMessage(String message) {
//         // Hàm này dùng để Server "nhắn tin" xuống máy Client (Real-time update)
//     }
// }
