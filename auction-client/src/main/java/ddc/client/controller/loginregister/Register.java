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

public class Register {
    @FXML
    private TextField usernameTextField, emailTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    @FXML
    public void register(ActionEvent event) {
        if (usernameTextField.getText().isEmpty() || passwordField.getText().isEmpty() || emailTextField.getText().isEmpty()) {
            errorLabel.setText("Bạn chưa điền thông tin vào chỗ trống.");
        }
        else {

        }
    }

    @FXML
    public void switchToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ddc/client/views/loginregister/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 400, 500));
            stage.show();
        } catch (IOException e) {}
    }
}
