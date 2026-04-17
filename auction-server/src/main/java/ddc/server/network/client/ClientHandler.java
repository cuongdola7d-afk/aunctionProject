// package ddc.server.network.client;
// import java.io.BufferedReader;
// import java.io.IOException;
// import java.io.PrintWriter;
// import java.net.Socket;
// import java.util.Map;

// import com.google.gson.Gson;

// import ddc.server.controller.AuctionController;
// import ddc.server.model.transaction.Auction;
// import ddc.server.model.user.Bidder;
// import ddc.server.network.message.MessageType;
// import ddc.server.network.message.SocketMessage;
// import ddc.server.network.request.PlaceBidRequest;
// import ddc.server.network.request.SubscribeAuctionRequest;
// import ddc.server.network.response.AuctionEventResponse;
// import ddc.server.network.response.ErrorResponse;
// import ddc.server.pattern.Singleton.AuctionManager;

// public class ClientHandler implements Runnable {
//     private final ClientConnection connection;
//     private final AuctionController auctionController;
//     private final AuctionEventDispatcher dispatcher;
//     private final Gson gson = new Gson();

//     public ClientHandler(
//             Socket socket,
//             BufferedReader reader,
//             PrintWriter writer,
//             AuctionController auctionController,
//             AuctionEventDispatcher dispatcher
//     ) {
//         this.connection = new ClientConnection(socket, reader, writer);
//         this.auctionController = auctionController;
//         this.dispatcher = dispatcher;
//     }

//     @Override
//     public void run() {
//         try {
//             String line;

//             while ((line = connection.getReader().readLine()) != null) {
//                 handleRawMessage(line);
//             }
//         } catch (Exception e) {
//             System.out.println("Client disconnected: " + e.getMessage());
//         } finally {
//             dispatcher.unsubscribeAll(connection);
//             connection.close();
//         }
//     }

//     private void handleRawMessage(String line) {
//         try {
//             SocketMessage message = gson.fromJson(line, SocketMessage.class);

//             if (message == null || message.getType() == null) {
//                 sendError("Invalid message.");
//                 return;
//             }

//             switch (message.getType()) {
//                 case SUBSCRIBE_AUCTION -> handleSubscribe(message.getPayloadJson());
//                 case PLACE_BID -> handlePlaceBid(message.getPayloadJson());
//                 default -> sendError("Unsupported message type: " + message.getType());
//             }
//         } catch (Exception e) {
//             sendError("Failed to parse message: " + e.getMessage());
//         }
//     }

// private void handleSubscribe(String payloadJson) {
//         SubscribeAuctionRequest request = gson.fromJson(payloadJson, SubscribeAuctionRequest.class);
//         if (request == null || request.getAuctionId() == null) {
//             sendError("auctionId is required.");
//             return;
//         }

//         // THAY THẾ: Gọi qua Manager thay vì dùng Map trực tiếp
//         Auction auction = AuctionManager.getInstance().getAuction(request.getAuctionId());
        
//         if (auction == null) {
//             sendError("Auction not found: " + request.getAuctionId());
//             return;
//         }

//         auctionController.handleRefreshStatus(auction);
//         dispatcher.subscribe(request.getAuctionId(), connection);

//         AuctionEventResponse snapshot = AuctionEventResponse.fromAuctionState(auction);
//         connection.send(MessageType.AUCTION_EVENT, snapshot, gson);
//     }

//     private void handlePlaceBid(String payloadJson) {
//         PlaceBidRequest request = gson.fromJson(payloadJson, PlaceBidRequest.class);
//         if (request == null) {
//             sendError("Invalid place bid request.");
//             return;
//         }

//         // THAY THẾ: Lấy dữ liệu thông qua Manager
//         Auction auction = AuctionManager.getInstance().getAuction(request.getAuctionId());
//         // Giả sử bạn cũng có UserManager hoặc làm tương tự cho Bidder
//         Bidder bidder = AuctionManager.getInstance().getBidder(request.getBidderId()); 

//         if (auction == null || bidder == null) {
//             sendError("Auction or Bidder not found.");
//             return;
//         }

//         try {
//             // Logic xử lý đặt giá (nơi sẽ tung ra Exception ở tuần 8)
//             auctionController.handlePlaceBid(auction, bidder, request.getAmount());
//         } catch (Exception e) {
//             sendError(e.getMessage()); // Trả lỗi về Client (ví dụ: "Giá thấp hơn hiện tại")
//         }
//     }

//     private void sendError(String message) {
//         connection.send(MessageType.ERROR, new ErrorResponse(message), gson);
//     }
// }
