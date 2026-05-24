package ddc.server.network.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ddc.server.config.GsonConfig;
import ddc.server.controller.service.AuctionService;
import ddc.server.controller.service.NotificationService;
import ddc.server.controller.service.WalletService;
import ddc.server.dao.AuctionDAO;
import ddc.server.dao.UserDAO;
import ddc.server.model.notification.NotificationType;
import ddc.server.model.transaction.Auction;
import ddc.server.model.user.Bidder;
import ddc.server.model.user.User;
import ddc.server.network.message.MessageType;
import ddc.server.network.message.SocketMessage;
import ddc.server.network.request.AuctionEventPayload;
import ddc.server.network.request.DashboardUpdatePayload;
import ddc.server.network.request.PlaceBidRequest;
import ddc.server.network.request.SubscribeAuctionRequest;
import ddc.server.network.response.ErrorPayload;
import ddc.server.pattern.Singleton.AuctionManager;

public class RealtimeClientHandler implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RealtimeClientHandler.class);
    private static final Set<ClientConnection> ACTIVE_CONNECTIONS = ConcurrentHashMap.newKeySet();

    private final Socket socket;
    private final AuctionService auctionService;
    private final AuctionManager auctionManager = AuctionManager.getInstance();
    private final AuctionDAO auctionDAO = new AuctionDAO();
    private final UserDAO userDAO = new UserDAO();
    private final WalletService walletService = new WalletService();
    private final Gson gson = GsonConfig.newGson();

    public RealtimeClientHandler(Socket socket, AuctionService auctionService) {
        this.socket = socket;
        this.auctionService = auctionService;
    }

    public static Set<ClientConnection> getActiveConnections() {
        return ACTIVE_CONNECTIONS;
    }

    public static boolean isUserOnline(String userId) {
        if (userId == null || userId.isBlank()) {
            return false;
        }
        for (ClientConnection conn : ACTIVE_CONNECTIONS) {
            if (userId.equals(conn.getUserId())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void run() {
        ClientConnection client = null;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            client = new ClientConnection(socket, reader, writer);
            ACTIVE_CONNECTIONS.add(client);
            LOGGER.info("Client connected: {}", client.getConnectionId());

            String line;
            while ((line = reader.readLine()) != null) {
                handleMessage(client, line);
            }
        } catch (Exception e) {
            LOGGER.warn("Client disconnected: {}", e.getMessage());
        } finally {
            if (client != null) {
                ACTIVE_CONNECTIONS.remove(client);
                client.unsubscribeAll();
                client.close();
                LOGGER.info("Client cleaned up and closed.");
            } else {
                closeSocket();
            }
        }
    }

    private void handleMessage(ClientConnection client, String line) {
        try {
            SocketMessage message = gson.fromJson(line, SocketMessage.class);

            if (message == null || message.getType() == null) {
                sendError(client, "Message khong hop le.");
                return;
            }

            LOGGER.info("Received message type: {} from client: {}", message.getType(), client.getConnectionId());

            switch (message.getType()) {
                case AUTH -> handleAuth(client, message);
                case SUBSCRIBE_AUCTION -> handleSubscribe(client, message);
                case PLACE_BID -> handlePlaceBid(client, message);
                default -> sendError(client, "Message type khong ho tro: " + message.getType());
            }
        } catch (Exception e) {
            LOGGER.warn("Message handling failed: {}", e.getMessage(), e);
            sendError(client, "Loi xu ly message: " + e.getMessage());
        }
    }

    private void handleAuth(ClientConnection client, SocketMessage message) {
        com.google.gson.JsonObject payload = gson.fromJson(message.getPayloadJson(), com.google.gson.JsonObject.class);
        if (payload != null && payload.has("userId")) {
            String userId = payload.get("userId").getAsString();
            client.setUserId(userId);
            LOGGER.info("Client {} authenticated as user {}", client.getConnectionId(), userId);
        }
    }

    public static void sendNotificationEventToUser(String userId, int unreadCount) {
        if (userId == null) {
            return;
        }
        com.google.gson.JsonObject payload = new com.google.gson.JsonObject();
        payload.addProperty("unreadCount", unreadCount);
        Gson gson = GsonConfig.newGson();
        for (ClientConnection connection : ACTIVE_CONNECTIONS) {
            if (userId.equals(connection.getUserId())) {
                try {
                    connection.send(MessageType.NOTIFICATION_EVENT, payload, gson);
                } catch (Exception e) {
                    LOGGER.warn("Failed to send notification event: {}", e.getMessage());
                }
            }
        }
    }

    private void handleSubscribe(ClientConnection client, SocketMessage message) {
        SubscribeAuctionRequest request = gson.fromJson(message.getPayloadJson(), SubscribeAuctionRequest.class);

        if (request == null || request.getAuctionId() == null || request.getAuctionId().isBlank()) {
            sendError(client, "auctionId khong hop le.");
            return;
        }

        String auctionId = request.getAuctionId();
        Auction auction = getAuctionOrLoad(auctionId);
        if (auction == null) {
            sendError(client, "Khong tim thay phien dau gia: " + auctionId);
            return;
        }

        client.subscribe(auctionId);
        sendAuctionEvent(client, buildSnapshot(auction));
        LOGGER.info("Client {} subscribed auction: {}", client.getConnectionId(), auctionId);
    }

    private void handlePlaceBid(ClientConnection client, SocketMessage message) {
        PlaceBidRequest request = gson.fromJson(message.getPayloadJson(), PlaceBidRequest.class);

        if (request == null) {
            sendError(client, "Bid request khong hop le.");
            return;
        }

        if (request.getAuctionId() == null || request.getAuctionId().isBlank()) {
            sendError(client, "auctionId khong hop le.");
            return;
        }

        if (request.getBidderId() == null || request.getBidderId().isBlank()) {
            sendError(client, "bidderId khong hop le.");
            return;
        }

        if (request.getAmount() <= 0) {
            sendError(client, "So tien bid phai lon hon 0.");
            return;
        }

        AuctionEventPayload event;
        String auctionId;
        String bidderName;

        Auction auction = getAuctionOrLoad(request.getAuctionId());
        if (auction == null) {
            sendError(client, "Khong tim thay phien dau gia: " + request.getAuctionId());
            return;
        }

        Bidder bidder = getBidderOrLoad(request.getBidderId());
        if (bidder == null) {
            sendError(client, "Khong tim thay bidder: " + request.getBidderId());
            return;
        }
        
        double availableBalance = walletService.getAvailableBalanceForBid(request.getBidderId(), request.getAuctionId());
        if (availableBalance < request.getAmount()) {
            sendError(client,
                    "So du kha dung khong du. So du kha dung: " + String.format("%,.0f", availableBalance)
                            + ", so tien bid: " + String.format("%,.0f", request.getAmount()));
            return;
        }

        String previousBidderId = auction.getHighestBidder() != null ? auction.getHighestBidder().getId() : null;

        boolean timeExtended;
        try {
            timeExtended = auctionService.placeBid(auction, bidder, request.getAmount(), LocalDateTime.now());
        } catch (Exception e) {
            sendError(client, e.getMessage());
            return;
        }

        synchronized (auction) {
            if (!auctionDAO.updateAuction(auction)) {
                sendError(client, "Khong cap nhat duoc phien dau gia.");
                return;
            }

            auctionId = auction.getId();
            bidderName = bidder.getUsername();

            event = new AuctionEventPayload();
            event.setEventType("NEW_BID");
            event.setAuctionId(auctionId);
            event.setCurrentPrice(auction.getCurrentPrice());
            event.setStatus(auction.getStatus().name());
            event.setBidderName(bidderName);
            event.setBidAmount(request.getAmount());
            event.setEndTime(auction.getEndTime() != null ? auction.getEndTime().toString() : null);
            event.setTimeExtended(timeExtended);
            event.setMinBidIncrement(auction.getMinBidIncrement());
            event.setMessage(
                    timeExtended ? "Bid moi: " + bidderName + " - " + request.getAmount() + "(thoi gian gia han)"
                            : "Bid moi: " + bidderName + " - " + request.getAmount());
        }

        broadcastAuctionEvent(auctionId, event);
        broadcastDashboardUpdate(auctionId, auction.getCurrentPrice(),
                auction.getStatus().name(),
                auction.getEndTime() != null ? auction.getEndTime().toString() : null);
        if (previousBidderId != null && !previousBidderId.equals(request.getBidderId())) {
            NotificationService notifService = new NotificationService();
            notifService.createNotification(previousBidderId, NotificationType.BID_OUTBID,
                    auctionId, "Bạn đã bị vượt giá!",
                    "Phiên " + auction.getItem().getItemName() + " có bid mới: " + request.getAmount());
        }
        LOGGER.info("Bid thanh cong: auction={} bidder={} amount={}", auctionId, bidderName, request.getAmount());
    }

    private void sendError(ClientConnection client, String errorMessage) {
        try {
            client.send(MessageType.ERROR, new ErrorPayload(errorMessage), gson);
        } catch (Exception e) {
            LOGGER.warn("Cannot send error to client: {}", e.getMessage());
        }
    }

    private void sendAuctionEvent(ClientConnection client, Object payload) {
        try {
            client.send(MessageType.AUCTION_EVENT, payload, gson);
        } catch (Exception e) {
            LOGGER.warn("Cannot send auction event to client: {}", e.getMessage());
        }
    }

    public static void broadcastAuctionEvent(String auctionId, Object payload) {
        Gson gson = GsonConfig.newGson();
        for (ClientConnection connection : ACTIVE_CONNECTIONS) {
            if (connection.isSubscribedTo(auctionId)) {
                try {
                    connection.send(MessageType.AUCTION_EVENT, payload, gson);
                } catch (Exception e) {
                    LOGGER.warn("Broadcast fail: {}", e.getMessage());
                }
            }
        }
    }

    // Gửi thông tin cập nhật dashboard tới TẤT CẢ connected clients (không cần subscribe)
    public static void broadcastDashboardUpdate(String auctionId, double currentPrice, String status, String endTime) {
        Gson gson = GsonConfig.newGson();
        DashboardUpdatePayload payload = new DashboardUpdatePayload(auctionId, currentPrice, status, endTime);
        for (ClientConnection connection : ACTIVE_CONNECTIONS) {
            try {
                connection.send(MessageType.DASHBOARD_UPDATE, payload, gson);
            } catch (Exception e) {
                LOGGER.warn("Dashboard broadcast fail: {}", e.getMessage());
            }
        }
    }

    // Yêu cầu tất cả client reload danh sách (khi có auction mới hoặc status thay đổi lớn)
    public static void broadcastDashboardRefresh() {
        Gson gson = GsonConfig.newGson();
        com.google.gson.JsonObject payload = new com.google.gson.JsonObject();
        payload.addProperty("action", "REFRESH");
        for (ClientConnection connection : ACTIVE_CONNECTIONS) {
            try {
                connection.send(MessageType.DASHBOARD_REFRESH, payload, gson);
            } catch (Exception e) {
                LOGGER.warn("Dashboard refresh broadcast fail: {}", e.getMessage());
            }
        }
    }

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

    private Bidder getBidderOrLoad(String bidderId) {
        Bidder bidder = auctionManager.getBidder(bidderId);

        if (bidder == null) {
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

        snapshot.setMinBidIncrement(auction.getMinBidIncrement());
        snapshot.setMessage("Snapshot phien dau gia.");
        return snapshot;
    }

    private void closeSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception ignored) {
        }
    }
}
