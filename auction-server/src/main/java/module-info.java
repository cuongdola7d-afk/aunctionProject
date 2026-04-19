module ddc.server {
    requires java.sql;
    requires com.google.gson;
    requires org.apiguardian.api;
    requires org.opentest4j;
    
    //Cấp quyền nạp thư viện JUnit (Giờ sẽ không báo đỏ nữa)
    requires org.junit.jupiter.api; 

    //Mở cửa các thư mục để Test có thể gọi đến
    exports ddc.server;
    exports ddc.server.service; // Bắt buộc để gọi được AuctionService
    exports ddc.server.model.transaction; // Bắt buộc để gọi được AuctionStatus
    exports ddc.server.model.item;
    exports ddc.server.model.user;
    exports ddc.server.exception;
    
    //Cho phép JUnit can thiệp sâu để chạy Test
    opens ddc.server.service to org.junit.platform.commons;
}