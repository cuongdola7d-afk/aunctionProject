module ddc.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.google.gson;
    requires javafx.base;
    requires org.slf4j;

    exports ddc.client;

    opens ddc.client to javafx.fxml, javafx.graphics;
    opens ddc.client.controller.notify to javafx.fxml;
    opens ddc.client.controller.loginregister to javafx.fxml, com.google.gson;
    opens ddc.client.controller.bidding to javafx.fxml;
    opens ddc.client.controller.selling to javafx.fxml;
    opens ddc.client.controller.home to javafx.fxml;
    opens ddc.client.controller.profile to javafx.fxml, com.google.gson;
    opens ddc.client.network.client to com.google.gson;
    opens ddc.client.network.response to com.google.gson;
    opens ddc.client.network.listener to com.google.gson;
    opens ddc.client.network.message to com.google.gson;
    opens ddc.client.network.request to com.google.gson;

    opens ddc.client.model to com.google.gson;
    opens ddc.client.model.ItemDTO to com.google.gson;
    opens ddc.client.model.ItemDTO.factory to com.google.gson;

    opens ddc.client.network to com.google.gson;
}