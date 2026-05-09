package ddc.client.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientContext {
    // Tạo một Executor sử dụng Virtual Threads cho toàn bộ App Client
    // Đây là "đội quân" sẽ xử lý mọi Task gửi lên Server
    public static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
    public static final String SERVER_HOST = System.getenv().getOrDefault("DDC_SERVER_HOST", "localhost");
    public static final int REALTIME_PORT = Integer.parseInt(
    System.getenv().getOrDefault("DDC_REALTIME_PORT", "5555"));
    // Ngăn chặn việc tạo đối tượng mới từ bên ngoài
    private ClientContext() {}
}
