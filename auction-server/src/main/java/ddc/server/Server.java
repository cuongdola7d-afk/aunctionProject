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

//     public static void main(String[] args) {
//     // 1. Khởi tạo DAO
//     ItemDAO itemDAO = new ItemDAO();

//     // 2. Chọn một ID chắc chắn đang có trong DB (xem trong MySQL Workbench)
//     String idCanTest = "I00009"; 

//     System.out.println("---------- ĐANG TEST GET ITEM ----------");
    
//     try {
//         // 3. Gọi hàm getItem
//         // Hàm này sẽ dùng Factory để tạo object và loadSpecificDetails để lấy data bảng phụ
//         ItemGeneric item = itemDAO.getItem(idCanTest);

//         // 4. Kiểm tra và in kết quả
//         if (item != null) {
//             System.out.println("Tìm thấy sản phẩm!");
            
//             // CHỈ CẦN DÒNG NÀY: Java sẽ tự gọi toString() của Art/Electronics tương ứng
//             System.out.println("Thông tin chi tiết: " + item); 
            
//         } else {
//             System.out.println("Không tìm thấy sản phẩm nào với ID: " + idCanTest);
//             System.out.println("Hãy kiểm tra lại tên cột (item_name, seller_name) trong DAO!");
//         }
//     } catch (Exception e) {
//         System.err.println("Có lỗi xảy ra trong quá trình truy vấn:");
//         e.printStackTrace();
//     }
    
//     System.out.println("----------------------------------------");
// }
}
