module ddc.client {
    requires javafx.controls;
    requires javafx.fxml;

    exports ddc.client;

    opens ddc.client to javafx.fxml, javafx.graphics;
    opens ddc.client.controller.loginregister to javafx.fxml;
    opens ddc.client.controller.bidding to javafx.fxml;
    opens ddc.client.controller.selling to javafx.fxml;
}