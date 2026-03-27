module com.auction {
    requires javafx.controls;
    requires javafx.fxml;

    // Chỉ cần mở gói (package) controller cho javafx.fxml
    opens com.auction.controller to javafx.fxml;
    
    // Xuất gói để các nơi khác có thể truy cập
    exports com.auction.controller;
}

