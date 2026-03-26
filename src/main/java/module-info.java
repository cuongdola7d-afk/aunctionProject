module com.aunction {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.auction.controller to javafx.fxml;

    exports com.auction.controller;

}