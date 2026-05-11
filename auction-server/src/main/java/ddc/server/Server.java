package ddc.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ddc.server.controller.service.AuctionService;
import ddc.server.network.RequestClientHandler;
import ddc.server.network.client.RealtimeClientHandler;

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

    private static final int IMAGE_PORT = 8081;


    private static final ExecutorService requestPool = Executors.newFixedThreadPool(100);
    private static final ExecutorService realtimePool = Executors.newVirtualThreadPerTaskExecutor();
    public static void main(String[] args) {

        /**
         * AuctionService dùng chung cho luồng realtime.
         *
         * Lý do:
         * - các client realtime cần thao tác trên cùng 1 service
         * - tránh mỗi client có 1 service riêng làm state bị lệch
         */
        AuctionService auctionService = new AuctionService();

        Thread requestServerThread = new Thread(() -> startRequestServer(), "request-server-8080");
        Thread realtimeServerThread = new Thread(() -> startRealtimeServer(auctionService), "realtime-server-5555");

        requestServerThread.start();
        realtimeServerThread.start();
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
            System.out.println("[" + Thread.currentThread().getName() + "] Request server opened at port " + REQUEST_PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                RequestClientHandler handler = new RequestClientHandler(clientSocket);
                
                // Thay vì new Thread().start(), hãy giao cho Pool xử lý
                requestPool.execute(handler);
            }
        } catch (Exception e) {
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
            System.out.println("[" + Thread.currentThread().getName() + "] Request server opened at port " + REALTIME_PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Virtual Thread sẽ tự động xử lý việc "đợi" mạng cho bạn
                realtimePool.execute(new RealtimeClientHandler(clientSocket, auctionService));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
