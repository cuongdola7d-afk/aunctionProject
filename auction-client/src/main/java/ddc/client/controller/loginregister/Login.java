package ddc.client.controller.loginregister;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Login {
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    @FXML
    public void login(ActionEvent event) {
        if (usernameTextField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            errorLabel.setText("Please enter your information.");
        }
        else {
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

    @FXML
    public void switchToRegister(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ddc/client/views/loginregister/Register.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 400, 500));
            stage.show();
        } catch (IOException e) {
            System.out.println("Error!" + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error!" + e.getMessage());
        }
    }
}
