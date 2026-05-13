package ddc.client.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SceneSwitcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(SceneSwitcher.class);

    public static void goTo(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(SceneSwitcher.class.getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            LOGGER.error("Loi chuyen scene: {}", fxmlPath, e);
        }
    }

    public static void goTo(MouseEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(SceneSwitcher.class.getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            LOGGER.error("Loi chuyen scene: {}", fxmlPath, e);
        }
    }
}