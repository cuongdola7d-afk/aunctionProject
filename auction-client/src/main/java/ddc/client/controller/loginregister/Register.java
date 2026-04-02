package ddc.client.controller.loginregister;

import ddc.client.controller.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Register {
    @FXML
    private TextField usernameTextField, emailTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    @FXML
    private void register(ActionEvent event) {
        if (usernameTextField.getText().isEmpty() || passwordField.getText().isEmpty() || emailTextField.getText().isEmpty()) {
            errorLabel.setText("Bạn chưa điền thông tin vào chỗ trống.");
        }
        else {

        }
    }

    @FXML
    private void switchToLogin(ActionEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/loginregister/Login.fxml");
    }
}
