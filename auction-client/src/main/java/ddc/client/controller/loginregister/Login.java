package ddc.client.controller.loginregister;

import java.io.IOException;

import ddc.client.controller.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Login {
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    @FXML
    private void login(ActionEvent event) {
        if (usernameTextField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            errorLabel.setText("Vui lòng nhập thông tin vào chỗ trống.");
        }
        else {
            try {
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
                Parent root = FXMLLoader.load(getClass().getResource("/ddc/client/views/selling/Selling.fxml"));
                Stage stage = new Stage();
                Image icon = new Image(getClass().getResourceAsStream("/ddc/client/views/DDCAuction.png"));

                stage.setTitle("DDC Auction");
                stage.getIcons().add(icon);
                stage.setResizable(true);
                stage.centerOnScreen();
                stage.setScene(new Scene(root, 800, 600));
                stage.show();
            } catch (IOException e) {
                System.out.println("IO Error!" + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error!" + e.getMessage());
            }
        }
    }

    @FXML
    private void switchToRegister(ActionEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/loginregister/Register.fxml");
    }
    
}
