module ddc.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;

    exports ddc.client;
    opens ddc.client.controller.loginregister to javafx.fxml;

    // opens gemini.client.controller to javafx.fxml;
    // opens gemini.client.model to com.google.gson; 
}