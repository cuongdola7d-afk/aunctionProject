package ddc.server.network;

import java.io.IOException;
import java.net.Socket;

// public class ClientHandler implements Runnable {
//     private Socket socket; // Đường dây liên lạc riêng với 1 khách
//     private AuctionServer server; // Để báo cáo lại cho Server

//     public ClientHandler(Socket socket, AuctionServer server) {
//         this.socket = socket;
//         this.server = server;
//     }

//     @Override
//     public void run() {
//         try {
//             // 1. Tạo bộ đọc và bộ ghi dữ liệu qua Socket
//             // 2. Vòng lặp while(true) để đợi lệnh từ Client
//             // 3. Nếu Client gửi lệnh BID -> Xử lý và gọi server.broadcast()
//         } catch (IOException e) {
//             // Xử lý khi khách hàng ngắt kết nối (tắt app)
//         }
//     }

//     public void sendMessage(String message) {
//         // Hàm này dùng để Server "nhắn tin" xuống máy Client (Real-time update)
//     }
// }