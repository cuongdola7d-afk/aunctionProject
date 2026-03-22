module com.aunction {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.aunction to javafx.fxml;
    exports com.aunction;
}
