module auction.client {
    requires javafx.controls;
    requires javafx.fxml;

    opens ddc.client to javafx.fxml;
    opens ddc.client.controller.bidding to javafx.fxml;
    opens ddc.client.model to javafx.base;

    exports ddc.client;
    exports ddc.client.controller.bidding;
    exports ddc.client.model;
}