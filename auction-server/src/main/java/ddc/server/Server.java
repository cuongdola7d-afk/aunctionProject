package ddc.server;

import java.net.ServerSocket;
import java.net.Socket;

import ddc.server.controller.service.AuctionService;
import ddc.server.network.ClientHandler;

public class Server {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        //Khởi tạo cục bộ các Service xử lý logic 
        AuctionService auctionService = new AuctionService();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server opened at port " + PORT);
            
            while (true) {
                // Chờ và đón Client kết nối
                Socket clientSocket = serverSocket.accept();
                System.out.println("New Client: " + clientSocket.getInetAddress());

                // Giao việc giao tiếp cho ClientHandler và TRUYỀN Service vào
                ClientHandler handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
            }

        } catch (Exception e) {
            System.out.println("Server Error! " + e.getMessage());
            e.printStackTrace();
        }
    }
}
