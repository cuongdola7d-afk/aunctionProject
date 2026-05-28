package ddc.server.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

import ddc.server.network.message.MessageType;
import ddc.server.network.message.SocketMessage;

public class ClientConnection {
    private final String connectionId;
    private String userId;
    private final Socket socket;
    private final BufferedReader reader;
    private final PrintWriter writer;
    private final Set<String> subscribedAuctionIds = ConcurrentHashMap.newKeySet();

    public ClientConnection(Socket socket, BufferedReader reader, PrintWriter writer) {
        this.connectionId = UUID.randomUUID().toString();
        this.socket = socket;
        this.reader = reader;
        this.writer = writer;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Socket getSocket() {
        return socket;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public void subscribe(String auctionId) {
        if (auctionId != null && !auctionId.isBlank()) {
            subscribedAuctionIds.add(auctionId);
        }
    }

    public void unsubscribeAll() {
        subscribedAuctionIds.clear();
    }

    public boolean isSubscribedTo(String auctionId) {
        return subscribedAuctionIds.contains(auctionId);
    }

    public synchronized void send(MessageType type, Object payload, Gson gson) throws IOException {
        String payloadJson = gson.toJson(payload);
        String jsonLine = gson.toJson(new SocketMessage(type, payloadJson));
        writer.println(jsonLine);

        if (writer.checkError()) {
            throw new IOException("Failed to send message to client " + connectionId);
        }
    }

    public void close() {
        try {
            reader.close();
        } catch (IOException ignored) {
        }

        writer.close();

        try {
            socket.close();
        } catch (IOException ignored) {
        }
    }
}