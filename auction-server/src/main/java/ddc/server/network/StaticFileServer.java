package ddc.server.network;

import io.undertow.Undertow;
import io.undertow.server.handlers.resource.FileResourceManager;
import java.io.File;
import static io.undertow.Handlers.resource;

public class StaticFileServer {
    private static Undertow server;

    public static void start(int port, String storagePath) {
        File storageDir = new File(storagePath);
        
        // Tạo thư mục nếu chưa tồn tại
        if (!storageDir.exists()) storageDir.mkdirs();

        server = Undertow.builder()
                .addHttpListener(port, "0.0.0.0") // Lắng nghe mọi kết nối đến cổng này
                .setHandler(resource(new FileResourceManager(storageDir, 1024 * 1024))
                        .setDirectoryListingEnabled(false)) // Bảo mật: không cho xem danh sách file
                .build();

        server.start();
        System.out.println(">>> IMAGE SERVER STARTED AT PORT " + port);
    }
}