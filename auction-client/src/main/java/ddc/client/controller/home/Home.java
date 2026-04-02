package ddc.client.controller.home;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class Home {
    @FXML
    private void switchToSelling (MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ddc/client/views/selling/Selling.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error!" + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error!" + e.getMessage());
        }
    }
}
