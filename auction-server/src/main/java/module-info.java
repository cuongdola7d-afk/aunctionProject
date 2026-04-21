module ddc.server {
    requires java.sql;
    requires com.google.gson;
    requires org.apiguardian.api;
    requires org.opentest4j;
    
    //Cấp quyền nạp thư viện JUnit (Giờ sẽ không báo đỏ nữa)
    requires org.junit.jupiter.api; 

    //Mở cửa các thư mục để Test có thể gọi đến
    exports ddc.server;

    opens ddc.server.controller to com.google.gson;
    opens ddc.server.controller.handler to com.google.gson;
    opens ddc.server.controller.service to com.google.gson, org.junit.platform.commons; // Bắt buộc để gọi được AuctionService, Cho phép JUnit can thiệp sâu để chạy Test

    opens ddc.server.model.transaction to com.google.gson; // Bắt buộc để gọi được AuctionStatus
    opens ddc.server.model.item to com.google.gson;
    opens ddc.server.model.user to com.google.gson;
    opens ddc.server.exception to com.google.gson;
    opens ddc.server.pattern.factory.ItemCreating to com.google.gson;
}