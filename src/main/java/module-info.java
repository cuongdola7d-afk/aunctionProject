module com.aunction {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.aunction.controller to javafx.fxml;
    exports com.aunction.controller;
}
