package ddc.client.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ddc.client.config.ClientContext;
import ddc.client.network.UserSession;
import ddc.client.network.listener.DashboardUpdateListener;
import ddc.client.network.message.MessageType;
import ddc.client.network.message.SocketMessage;
import ddc.client.network.response.DashboardUpdatePayload;
import javafx.application.Platform;

public class GlobalSocketClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalSocketClient.class);
    private static GlobalSocketClient instance;
    private final Gson gson = new Gson();
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private volatile boolean connected = false;

    // Danh sách listener cho event DASHBOARD_UPDATE (thread-safe)
    private final Set<DashboardUpdateListener> dashboardListeners = ConcurrentHashMap.newKeySet();

    private GlobalSocketClient() {}

    public static synchronized GlobalSocketClient getInstance() {
        if (instance == null) {
            instance = new GlobalSocketClient();
        }
        return instance;
    }

    // Đăng ký listener nhận event DASHBOARD_UPDATE
    public void addDashboardListener(DashboardUpdateListener listener) {
        if (listener != null) {
            dashboardListeners.add(listener);
        }
    }

    // Hủy đăng ký listener khi rời màn hình
    public void removeDashboardListener(DashboardUpdateListener listener) {
        dashboardListeners.remove(listener);
    }

    public synchronized void connect() {
        if (connected) return;
        try {
            socket = new Socket(ClientContext.SERVER_HOST, ClientContext.REALTIME_PORT);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            connected = true;

            // Send AUTH message
            String userId = UserSession.getInstance().getId();
            if (userId != null && !userId.isBlank()) {
                JsonObject payload = new JsonObject();
                payload.addProperty("userId", userId);
                String line = gson.toJson(new SocketMessage(MessageType.AUTH, gson.toJson(payload)));
                writer.println(line);
                if (writer.checkError()) {
                    LOGGER.warn("Failed to send AUTH");
                }
            }

            Thread.ofVirtual().name("global-socket-reader-").start(this::readLoop);

            Thread.ofVirtual().name("global-socket-heartbeat-").start(this::heartbeatLoop);
            
            LOGGER.info("GlobalSocketClient connected for userId {}", userId);
        } catch (IOException e) {
            LOGGER.error("GlobalSocketClient connect failed", e);
            connected = false;
        }
    }

    private void readLoop() {
        try {
            String line;
            while (connected && (line = reader.readLine()) != null) {
                handleRawMessage(line);
            }
        } catch (java.net.SocketException e) {
            if (!connected) {
                LOGGER.info("Socket Disconnected Actively :)");
            } else {
                LOGGER.warn("Lost Socket Connection :(", e);
            }
        } catch (Exception e) {
            LOGGER.warn("GlobalSocketClient read error", e);
        } finally {
            disconnect();
        }
    }

    private void heartbeatLoop() {
        LOGGER.info("Heartbeat loop started.");
        try {
            // Vòng lặp sẽ chạy liên tục miễn là trạng thái connected còn true
            while (connected) {
                Thread.sleep(5000); // Đợi 5 giây (Sử dụng Virtual Thread nên sleep không tốn tài nguyên)
                
                if (connected) {
                    sendPing();
                }
            }
        } catch (InterruptedException e) {
            LOGGER.info("Heartbeat loop interrupted.");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            LOGGER.warn("Error in heartbeat loop", e);
        } finally {
            LOGGER.info("Heartbeat loop stopped.");
        }
    }

    private synchronized void sendPing() {
        if (!connected || writer == null) return;
        try {
            // Tạo SocketMessage với Type là PING, payload có thể để rỗng hoặc null tùy constructor của bạn
            SocketMessage pingMessage = new SocketMessage(MessageType.PING, "{}");
            
            String line = gson.toJson(pingMessage);
            writer.println(line);
            
            if (writer.checkError()) {
                LOGGER.warn("Failed to send PING heartbeat (Stream error)");
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to send PING heartbeat: {}", e.getMessage());
        }
    }

    private void handleRawMessage(String line) {
        SocketMessage message = gson.fromJson(line, SocketMessage.class);
        if (message == null || message.getType() == null) return;

        switch (message.getType()) {
            case NOTIFICATION_EVENT -> {
                JsonObject payload = gson.fromJson(message.getPayloadJson(), JsonObject.class);
                if (payload.has("unreadCount")) {
                    int unreadCount = payload.get("unreadCount").getAsInt();
                    Platform.runLater(() -> UserSession.getInstance().setUnreadCount(unreadCount));
                }
            }
            case DASHBOARD_UPDATE -> {
                DashboardUpdatePayload payload = gson.fromJson(message.getPayloadJson(), DashboardUpdatePayload.class);
                if (payload != null && payload.getAuctionId() != null) {
                    // Gọi tất cả listener đã đăng ký
                    for (DashboardUpdateListener listener : dashboardListeners) {
                        try {
                            listener.onDashboardUpdate(
                                payload.getAuctionId(),
                                payload.getCurrentPrice(),
                                payload.getStatus(),
                                payload.getEndTime()
                            );
                        } catch (Exception e) {
                            LOGGER.warn("Dashboard listener error", e);
                        }
                    }
                }
            }
            case DASHBOARD_REFRESH -> {
                // Yêu cầu client reload toàn bộ danh sách
                for (DashboardUpdateListener listener : dashboardListeners) {
                    try {
                        listener.onDashboardRefresh();
                    } catch (Exception e) {
                        LOGGER.warn("Dashboard refresh listener error", e);
                    }
                }
            }
            default -> LOGGER.debug("Unhandled message type: {}", message.getType());
        }
    }

    public synchronized void disconnect() {
        connected = false;
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
        try { if (reader != null) reader.close(); } catch (IOException ignored) {}
        if (writer != null) writer.close();

    }
}
