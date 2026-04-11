package ddc.server.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.AuctionStatus;

public class AuctionServer {
    private static List<Auction> auctionList = new ArrayList<>(); // Danh sách phiên đấu giá

    public static void main(String[] args) {
        // 1. Kích hoạt "Ông quản gia" chạy song song
        // startAutoScheduler();

        // 2. Mở cổng để Client kết nối
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            System.out.println("Server đấu giá đang chạy trên cổng 8888...");

            // while (true) {
            //     Socket clientSocket = serverSocket.accept();
            //     // Mỗi người dùng vào sẽ được cấp 1 luồng riêng
            //     new Thread(new ClientHandler(clientSocket)).start();
            // }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // private static void startAutoScheduler() {
    //     Thread scheduler = new Thread(() -> {
    //         while (true) {
    //             LocalDateTime now = LocalDateTime.now();
    //             for (Auction a : auctionList) {
    //                 // Logic: OPEN -> RUNNING [cite: 55]
    //                 if (a.getStatus() == AuctionStatus.OPEN && !now.isBefore(a.getStartTime())) {
    //                     a.startAuction();
    //                     System.out.println("Phiên " + a.getId() + " bắt đầu!");
    //                 }
    //                 // Logic: RUNNING -> FINISHED [cite: 53, 55]
    //                 if (a.getStatus() == AuctionStatus.RUNNING && !now.isBefore(a.getEndTime())) {
    //                     a.endAuction();
    //                     System.out.println("Phiên " + a.getId() + " kết thúc!");
    //                 }
    //             }
    //             try { Thread.sleep(1000); } catch (InterruptedException e) {}
    //         }
    //     });
    //     scheduler.setDaemon(true); // Để luồng này tự tắt khi Server dừng
    //     scheduler.start();
    // }
}
