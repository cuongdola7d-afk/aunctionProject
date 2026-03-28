module com.aunction {
    requires javafx.controls;
    requires javafx.fxml;

    opens ddc.client.controller.bidding to javafx.fxml;
    exports ddc.client;
}