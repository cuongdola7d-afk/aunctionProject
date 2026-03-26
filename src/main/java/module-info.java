module com.auction {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.auction.controller to javafx.fxml;
    exports com.auction.controller;
    opens com.auction.controller.login to javafx.fxml;
    exports com.auction.controller.login;
}

