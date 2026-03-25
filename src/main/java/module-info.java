module com.aunction {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.aunction.controller to javafx.fxml;

    exports com.aunction.controller;
    exports com.aunction.entity.auction;
    exports com.aunction.entity.item;
    exports com.aunction.entity.user;
    exports com.aunction.service;
    exports com.aunction.exception;
}