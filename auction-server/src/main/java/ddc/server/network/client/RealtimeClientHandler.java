package ddc.server.network.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import ddc.server.config.GsonConfig;

import ddc.server.controller.service.AuctionService;
import ddc.server.dao.AuctionDAO;
import ddc.server.dao.UserDAO;
import ddc.server.model.transaction.Auction;
import ddc.server.model.user.Bidder;
import ddc.server.model.user.User;
import ddc.server.network.message.MessageType;
import ddc.server.network.message.SocketMessage;
import ddc.server.network.request.PlaceBidRequest;
import ddc.server.network.request.SubscribeAuctionRequest;
import ddc.server.network.response.AuctionEventPayload;
import ddc.server.network.response.ErrorPayload;
import ddc.server.pattern.Singleton.AuctionManager;


public class RealtimeClientHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(RealtimeClientHandler.class.getName());
    private static final Set<ClientConnection> ACTIVE_CONNECTIONS = ConcurrentHashMap.newKeySet();

    private final Socket socket;
    private final AuctionService auctionService;
    private final AuctionManager auctionManager = AuctionManager.getInstance();
    private final AuctionDAO auctionDAO = new AuctionDAO();
    private final UserDAO userDAO = new UserDAO();
    private final Gson gson = GsonConfig.newGson();

    public RealtimeClientHandler(Socket socket, AuctionService auctionService) {
        this.socket = socket;
        this.auctionService = auctionService;
    }

    public static Set<ClientConnection> getActiveConnections() {
        return ACTIVE_CONNECTIONS;
    }

    @Override
    public void run() {
        ClientConnection client = null;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            client = new ClientConnection(socket, reader, writer);
            ACTIVE_CONNECTIONS.add(client);
            LOGGER.info("Client kết nối: " + client.getConnectionId());

            String line;
            while ((line = reader.readLine()) != null) {
                handleMessage(client, line);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Client ngắt kết nối: " + e.getMessage());
        } finally {
            if (client != null) {
                ACTIVE_CONNECTIONS.remove(client);
                client.unsubscribeAll();
                client.close();
                LOGGER.info("Client đã dọn dẹp và đóng.");
            } else {
                try {
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }
                } catch (Exception e) {
                        // bỏ qua
                }
            }
        }
    }

// Parse JSON line thành SocketMessage, dispatch theo type
private void handleMessage(ClientConnection client, String line) {
    try {
        SocketMessage message = gson.fromJson(line, SocketMessage.class);

        if (message == null || message.getType() == null) {
            sendError(client, "Message không hợp lệ.");
            return;
        }

        LOGGER.info("Nhận message type: " + message.getType()
                + " từ client: " + client.getConnectionId());

        switch (message.getType()) {
            case SUBSCRIBE_AUCTION -> handleSubscribe(client, message);
            case PLACE_BID        -> handlePlaceBid(client, message);
            default               -> sendError(client, "Message type không hỗ trợ: " + message.getType());
        }
    } catch (Exception e) {
        LOGGER.log(Level.WARNING, "Lỗi xử lý message: " + e.getMessage(), e);
        sendError(client, "Lỗi xử lý message: " + e.getMessage());
    }
}
  
// Xử lý subscribe auction — load auction + gửi snapshot
private void handleSubscribe(ClientConnection client, SocketMessage message) {
    SubscribeAuctionRequest request =
            gson.fromJson(message.getPayloadJson(), SubscribeAuctionRequest.class);

    if (request == null || request.getAuctionId() == null || request.getAuctionId().isBlank()) {
        sendError(client, "auctionId không hợp lệ.");
        return;
    }

    String auctionId = request.getAuctionId();

    // Đăng ký client theo dõi auction này
    client.subscribe(auctionId);

    // Lấy auction từ RAM, nếu chưa có thì load từ DB
    Auction auction = getAuctionOrLoad(auctionId);

    if (auction == null) {
        sendError(client, "Không tìm thấy phiên đấu giá: " + auctionId);
        return;
    }

    // Gửi snapshot về cho client
    AuctionEventPayload snapshot = buildSnapshot(auction);
    sendAuctionEvent(client, snapshot);

    LOGGER.info("Client " + client.getConnectionId() + " đã subscribe auction: " + auctionId);
}

// Xử lý đặt bid — validate + update state + broadcast
private void handlePlaceBid(ClientConnection client, SocketMessage message) {
    PlaceBidRequest request =
            gson.fromJson(message.getPayloadJson(), PlaceBidRequest.class);

    // Validate input
    if (request == null) {
        sendError(client, "Bid request không hợp lệ.");
        return;
    }

    if (request.getAuctionId() == null || request.getAuctionId().isBlank()) {
        sendError(client, "auctionId không hợp lệ.");
        return;
    }

    if (request.getBidderId() == null || request.getBidderId().isBlank()) {
        sendError(client, "bidderId không hợp lệ.");
        return;
    }

    if (request.getAmount() <= 0) {
        sendError(client, "Số tiền bid phải lớn hơn 0.");
        return;
    }

    // Lấy auction và bidder
    Auction auction = getAuctionOrLoad(request.getAuctionId());
    if (auction == null) {
        sendError(client, "Không tìm thấy phiên đấu giá: " + request.getAuctionId());
        return;
    }

    Bidder bidder = getBidderOrLoad(request.getBidderId());
    if (bidder == null) {
        sendError(client, "Không tìm thấy bidder: " + request.getBidderId());
        return;
    }

    // Gọi AuctionService để validate logic nghiệp vụ + đặt bid
    try {
        auctionService.placeBid(auction, bidder, request.getAmount(), LocalDateTime.now());
    } catch (Exception e) {
        sendError(client, e.getMessage());
        return;
    }

    // Sync về DB
    auctionDAO.updateAuction(auction);

    // Build event và broadcast
    AuctionEventPayload event = new AuctionEventPayload();
    event.setEventType("NEW_BID");
    event.setAuctionId(auction.getId());
    event.setCurrentPrice(auction.getCurrentPrice());
    event.setStatus(auction.getStatus().name());
    event.setBidderName(bidder.getUsername());
    event.setBidAmount(request.getAmount());
    event.setMessage("Bid mới: " + bidder.getUsername() + " - " + request.getAmount());

    broadcastAuctionEvent(auction.getId(), event);

    LOGGER.info("Bid thành công: auction=" + auction.getId()
            + " bidder=" + bidder.getUsername()
            + " amount=" + request.getAmount());
}

// ========== HELPER METHODS ==========

// Gửi error message về 1 client
private void sendError(ClientConnection client, String errorMessage) {
    try {
        client.send(MessageType.ERROR, new ErrorPayload(errorMessage), gson);
    } catch (Exception e) {
        LOGGER.log(Level.WARNING, "Không gửi được error cho client: " + e.getMessage());
    }
}

// Gửi auction event về 1 client
private void sendAuctionEvent(ClientConnection client, Object payload) {
    try {
        client.send(MessageType.AUCTION_EVENT, payload, gson);
    } catch (Exception e) {
        LOGGER.log(Level.WARNING, "Không gửi được event cho client: " + e.getMessage());
    }
}

// Broadcast auction event cho tất cả client đang subscribe auctionId
private void broadcastAuctionEvent(String auctionId, Object payload) {
    for (ClientConnection connection : ACTIVE_CONNECTIONS) {
        if (connection.isSubscribedTo(auctionId)) {
            sendAuctionEvent(connection, payload);
        }
    }
}

// Lấy auction từ AuctionManager (RAM), nếu chưa có thì load từ DB rồi cache lại
private Auction getAuctionOrLoad(String auctionId) {
    Auction auction = auctionManager.getAuction(auctionId);

    if (auction == null) {
        auction = auctionDAO.getAuctionById(auctionId);
        if (auction != null) {
            auctionManager.addAuction(auction);
        }
    }

    return auction;
}

// Lấy bidder từ AuctionManager (RAM), nếu chưa có thì load từ DB rồi cache lại
private Bidder getBidderOrLoad(String bidderId) {
    Bidder bidder = auctionManager.getBidder(bidderId);

    if (bidder == null) {
        // UserDAO.getUser trả về User, cần convert sang Bidder
        User user = userDAO.getUserById(bidderId);
        if (user != null) {
            bidder = new Bidder();
            bidder.setId(user.getId());
            bidder.setUsername(user.getUsername());
            bidder.setName(user.getName());
            bidder.setEmail(user.getEmail());
            auctionManager.addBidder(bidder);
        }
    }

    return bidder;
}

// Build snapshot payload từ Auction object
private AuctionEventPayload buildSnapshot(Auction auction) {
    AuctionEventPayload snapshot = new AuctionEventPayload();
    snapshot.setEventType("SNAPSHOT");
    snapshot.setAuctionId(auction.getId());
    snapshot.setCurrentPrice(auction.getCurrentPrice());
    snapshot.setStatus(auction.getStatus().name());
    snapshot.setStartTime(auction.getStartTime() != null ? auction.getStartTime().toString() : null);
    snapshot.setEndTime(auction.getEndTime() != null ? auction.getEndTime().toString() : null);

    if (auction.getHighestBidder() != null) {
        snapshot.setBidderName(auction.getHighestBidder().getUsername());
    }

    snapshot.setMessage("Snapshot phiên đấu giá.");
    return snapshot;
}


}