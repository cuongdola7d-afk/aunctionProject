package ddc.client.network.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import ddc.client.network.listener.ServerMessageListener;
import ddc.client.network.message.MessageType;
import ddc.client.network.message.SocketMessage;
import ddc.client.network.request.PlaceBidRequest;
import ddc.client.network.request.SubscribeAuctionRequest;
import ddc.client.network.response.AuctionEventResponse;
import ddc.client.network.response.ErrorResponse;

public class AuctionSocketClient {
    private final String host;
    private final int port;
    private final Gson gson = new Gson();

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Thread readerThread;

    private volatile boolean connected = false;
    private ServerMessageListener listener;

    public AuctionSocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setListener(ServerMessageListener listener) {
        this.listener = listener;
    }

    public synchronized void connect() throws IOException {
        if (connected) {
            return;
        }

        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        connected = true;

        readerThread = new Thread(this::readLoop, "auction-client-reader");
        readerThread.setDaemon(true);
        readerThread.start();
    }

    public synchronized void disconnect() {
        connected = false;

        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ignored) {
        }

        if (writer != null) {
            writer.close();
        }

        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException ignored) {
        }
    }

    public boolean isConnected() {
        return connected && socket != null && socket.isConnected() && !socket.isClosed();
    }

    public void subscribeAuction(String auctionId) {
        ensureConnected();
        send(MessageType.SUBSCRIBE_AUCTION, new SubscribeAuctionRequest(auctionId));
    }

    public void placeBid(String auctionId, String bidderId, double amount) {
        ensureConnected();
        send(MessageType.PLACE_BID, new PlaceBidRequest(auctionId, bidderId, amount));
    }

    private synchronized void send(MessageType type, Object payload) {
        String payloadJson = gson.toJson(payload);
        String line = gson.toJson(new SocketMessage(type, payloadJson));
        writer.println(line);

        if (writer.checkError()) {
            throw new RuntimeException("Không gửi được message tới server.");
        }
    }

    private void readLoop() {
        try {
            String line;

            while (connected && (line = reader.readLine()) != null) {
                handleRawMessage(line);
            }

            if (connected && listener != null) {
                listener.onDisconnected("Mất kết nối tới server.");
            }

        } catch (Exception e) {
            if (connected && listener != null) {
                listener.onDisconnected("Lỗi đọc dữ liệu từ server: " + e.getMessage());
            }
        } finally {
            disconnect();
        }
    }

    private void handleRawMessage(String line) {
        SocketMessage message = gson.fromJson(line, SocketMessage.class);

        if (message == null || message.getType() == null) {
            if (listener != null) {
                listener.onError("Message từ server không hợp lệ.");
            }
            return;
        }

        switch (message.getType()) {
            case AUCTION_EVENT -> {
                AuctionEventResponse event =
                        gson.fromJson(message.getPayloadJson(), AuctionEventResponse.class);
                if (listener != null) {
                    listener.onAuctionEvent(event);
                }
            }
            case ERROR -> {
                ErrorResponse error =
                        gson.fromJson(message.getPayloadJson(), ErrorResponse.class);
                if (listener != null) {
                    listener.onError(error != null ? error.getMessage() : "Lỗi không xác định.");
                }
            }
            default -> {
                if (listener != null) {
                    listener.onError("Loại message không hỗ trợ: " + message.getType());
                }
            }
        }
    }

    private void ensureConnected() {
        if (!isConnected()) {
            throw new IllegalStateException("Client chưa kết nối tới server.");
        }
    }
}