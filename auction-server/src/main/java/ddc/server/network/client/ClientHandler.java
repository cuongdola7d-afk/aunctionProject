package ddc.server.network.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import ddc.server.service.AuctionService; // Import Service 

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final AuctionService auctionService; // Lưu trữ biến service

    // Cập nhật hàm khởi tạo để nhận thêm AuctionService
    public ClientHandler(Socket socket, AuctionService auctionService) {
        this.socket = socket;
        this.auctionService = auctionService;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

            String message;
            // Lắng nghe tin nhắn từ Client
            while ((message = reader.readLine()) != null) {
                System.out.println("Client gửi yêu cầu: " + message);

                writer.println("Server đã nhận: " + message);
            }
        } catch (Exception e) {
            System.out.println("Client ngắt kết nối: " + e.getMessage());
        } finally {
            try {
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (Exception e) {
                // Bỏ qua lỗi khi đóng socket
            }
        }
    }
}