package ddc.server;

import java.net.ServerSocket;
import java.net.Socket;

import ddc.server.network.RequestClientHandler;
import ddc.server.network.client.RealtimeClientHandler;
import ddc.server.service.AuctionService;

public class Server {

    /**
     * Cổng xử lý request-response thông thường.
     *
     * Dùng cho các chức năng như:
     * - login
     * - register
     * - add item
     * - các request gửi 1 lần và nhận 1 lần
     */
    private static final int REQUEST_PORT = 8080;

    /**
     * Cổng realtime cho bidding.
     *
     * Dùng cho các chức năng như:
     * - subscribe auction
     * - place bid
     * - nhận event realtime từ server
     */
    private static final int REALTIME_PORT = 5555;

    public static void main(String[] args) {

        /**
         * AuctionService dùng chung cho luồng realtime.
         *
         * Lý do:
         * - các client realtime cần thao tác trên cùng 1 service
         * - tránh mỗi client có 1 service riêng làm state bị lệch
         */
        AuctionService auctionService = new AuctionService();

        /**
         * Thread chạy server cổng 8080.
         * Server này chuyên nhận request thường.
         */
        Thread requestServerThread = new Thread(
                () -> startRequestServer(),
                "request-server-8080"
        );

        /**
         * Thread chạy server cổng 5555.
         * Server này chuyên nhận kết nối realtime cho đấu giá.
         */
        Thread realtimeServerThread = new Thread(
                () -> startRealtimeServer(auctionService),
                "realtime-server-5555"
        );

        requestServerThread.start();
        realtimeServerThread.start();

        System.out.println("Server system started:");
        System.out.println("- Request server:  " + REQUEST_PORT);
        System.out.println("- Realtime server: " + REALTIME_PORT);
    }

    /**
     * Khởi động server request-response thông thường ở cổng 8080.
     *
     * Luồng hoạt động:
     * Client -> 8080 -> RequestClientHandler
     *
     * Mỗi client kết nối vào sẽ được giao cho 1 thread riêng để xử lý.
     */
    private static void startRequestServer() {
        try (ServerSocket serverSocket = new ServerSocket(REQUEST_PORT)) {
            System.out.println("Request server opened at port " + REQUEST_PORT);

            while (true) {
                // Chờ client thường kết nối vào cổng 8080
                Socket clientSocket = serverSocket.accept();
                System.out.println("[8080] New Client: " + clientSocket.getInetAddress());

                // Tạo handler riêng cho client này
                RequestClientHandler handler = new RequestClientHandler(clientSocket);

                // Chạy handler trên thread riêng để không block các client khác
                new Thread(handler).start();
            }
        } catch (Exception e) {
            System.out.println("Request Server Error! " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Khởi động server realtime ở cổng 5555.
     *
     * Luồng hoạt động:
     * AuctionSocketClient / ManualSocketClient -> 5555 -> RealtimeClientHandler
     *
     * Mỗi client realtime sẽ có 1 thread riêng.
     * Các handler này dùng chung AuctionService để xử lý bid và phát event.
     */
    private static void startRealtimeServer(AuctionService auctionService) {
        try (ServerSocket serverSocket = new ServerSocket(REALTIME_PORT)) {
            System.out.println("Realtime server opened at port " + REALTIME_PORT);

            while (true) {
                // Chờ client realtime kết nối vào cổng 5555
                Socket clientSocket = serverSocket.accept();
                System.out.println("[5555] New Realtime Client: " + clientSocket.getInetAddress());

                // Tạo handler realtime cho client này
                RealtimeClientHandler handler =
                        new RealtimeClientHandler(clientSocket, auctionService);

                // Chạy handler trên thread riêng
                new Thread(handler).start();
            }

        } catch (Exception e) {
            System.out.println("Realtime Server Error! " + e.getMessage());
            e.printStackTrace();
        }
    }
}
