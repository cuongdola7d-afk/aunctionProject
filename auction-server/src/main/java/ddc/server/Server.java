package ddc.server;

import java.net.ServerSocket;
import java.net.Socket;

import ddc.server.network.client.ClientHandler;
import ddc.server.service.AuctionService; // Import Service

public class Server {
    private static final int PORT = 13354;

    public static void main(String[] args) {
        //Khởi tạo cục bộ các Service xử lý logic 
        AuctionService auctionService = new AuctionService();
        

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server đã khởi động trên cổng: " + PORT);

            while (true) {
                // Chờ và đón Client kết nối
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client mới kết nối: " + clientSocket.getInetAddress());

                // Giao việc giao tiếp cho ClientHandler và TRUYỀN Service vào
                ClientHandler handler = new ClientHandler(clientSocket, auctionService);
                new Thread(handler).start();
            }

        } catch (Exception e) {
            System.out.println("Lỗi khởi chạy Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}