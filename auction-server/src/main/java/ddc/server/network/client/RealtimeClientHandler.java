package ddc.server.network.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ddc.server.controller.service.AuctionService;

public class RealtimeClientHandler implements Runnable {
    private static final Set<ClientConnection> ACTIVE_CONNECTIONS =
            ConcurrentHashMap.newKeySet();

    private final Socket socket;
    private final AuctionService auctionService;

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

            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Client gửi yêu cầu: " + message);
                writer.println("Server đã nhận: " + message);
            }
        } catch (Exception e) {
            System.out.println("Client ngắt kết nối: " + e.getMessage());
        } finally {
            if (client != null) {
                ACTIVE_CONNECTIONS.remove(client);
                client.unsubscribeAll();
                client.close();
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
}