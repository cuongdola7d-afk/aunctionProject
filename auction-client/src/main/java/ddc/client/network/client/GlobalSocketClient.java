package ddc.client.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ddc.client.config.ClientContext;
import ddc.client.network.UserSession;
import ddc.client.network.message.MessageType;
import ddc.client.network.message.SocketMessage;
import javafx.application.Platform;

public class GlobalSocketClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalSocketClient.class);
    private static GlobalSocketClient instance;
    private final Gson gson = new Gson();
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private volatile boolean connected = false;

    private GlobalSocketClient() {}

    public static synchronized GlobalSocketClient getInstance() {
        if (instance == null) {
            instance = new GlobalSocketClient();
        }
        return instance;
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

    private void handleRawMessage(String line) {
        SocketMessage message = gson.fromJson(line, SocketMessage.class);
        if (message != null && message.getType() == MessageType.NOTIFICATION_EVENT) {
            JsonObject payload = gson.fromJson(message.getPayloadJson(), JsonObject.class);
            if (payload.has("unreadCount")) {
                int unreadCount = payload.get("unreadCount").getAsInt();
                Platform.runLater(() -> UserSession.getInstance().setUnreadCount(unreadCount));
            }
        }
    }

    public synchronized void disconnect() {
        connected = false;
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
        try { if (reader != null) reader.close(); } catch (IOException ignored) {}
        if (writer != null) writer.close();

    }
}
