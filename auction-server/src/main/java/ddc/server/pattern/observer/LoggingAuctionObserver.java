package ddc.server.pattern.observer;

import java.time.format.DateTimeFormatter;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ddc.server.config.GsonConfig;
import ddc.server.network.client.ClientConnection;
import ddc.server.network.message.MessageType;

public class LoggingAuctionObserver implements AuctionObserver {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Set<ClientConnection> activeConnections;
    private final Gson gson;

    public LoggingAuctionObserver(Set<ClientConnection> activeConnections) {
        this(activeConnections, GsonConfig.newGson());
    }

    public LoggingAuctionObserver(Set<ClientConnection> activeConnections, Gson gson) {
        this.activeConnections = activeConnections;
        this.gson = gson;
    }

    @Override
    public void update(AuctionEvent event) {
        if (event == null) {
            return;
        }

        // 1) Log ra console để debug
        System.out.println(
                "[AuctionObserver] type=" + event.getType()
                        + ", auctionId=" + event.getAuctionId()
                        + ", bidder=" + event.getBidderName()
                        + ", bidAmount=" + event.getBidAmount()
                        + ", currentPrice=" + event.getCurrentPrice()
                        + ", status=" + event.getStatus()
                        + ", message=" + event.getMessage()
        );

        // 2) Nếu không có auctionId thì không broadcast được
        if (event.getAuctionId() == null || event.getAuctionId().isBlank()) {
            return;
        }

        // 3) Nếu chưa có client nào kết nối thì thôi
        if (activeConnections == null || activeConnections.isEmpty()) {
            return;
        }

        // 4) Đổi AuctionEvent -> payload JSON đúng format client đang parse
        JsonObject payload = toClientPayload(event);

        // 5) Chỉ broadcast tới các client đang subscribe auction này
        for (ClientConnection connection : activeConnections) {
            if (connection == null) {
                continue;
            }

            if (!connection.isSubscribedTo(event.getAuctionId())) {
                continue;
            }

            try {
                connection.send(MessageType.AUCTION_EVENT, payload, gson);
            } catch (Exception e) {
                System.out.println(
                        "[AuctionObserver] Broadcast lỗi tới client "
                                + connection.getConnectionId()
                                + ": " + e.getMessage()
                );
            }
        }
    }

    private JsonObject toClientPayload(AuctionEvent event) {
        JsonObject json = new JsonObject();

        json.addProperty("eventType", mapEventType(event.getType()));
        json.addProperty("auctionId", event.getAuctionId());
        json.addProperty("itemId", event.getItemId());
        json.addProperty("itemName", event.getItemName());
        json.addProperty("bidderName", event.getBidderName());
        json.addProperty("bidAmount", event.getBidAmount());
        json.addProperty("currentPrice", event.getCurrentPrice());

        json.addProperty(
                "status",
                event.getStatus() != null ? event.getStatus().name() : null
        );

        json.addProperty(
                "eventTime",
                event.getEventTime() != null ? FORMATTER.format(event.getEventTime()) : null
        );

        json.addProperty("message", event.getMessage());

        return json;
    }

    private String mapEventType(AuctionEventType type) {
        if (type == null) {
            return "UNKNOWN";
        }

        return switch (type) {
            case NEW_BID -> "NEW_BID";
            case AUCTION_STARTED -> "AUCTION_STARTED";
            case AUCTION_FINISHED -> "AUCTION_FINISHED";
            case AUCTION_CANCELLED -> "AUCTION_CANCELLED";
            case STATUS_CHANGED -> "STATUS_CHANGED";
        };
    }
}