package ddc.client.controller.loginregister;

import ddc.client.controller.SceneSwitcher;
import ddc.client.model.UserDTO;
import ddc.client.network.ClientToServer;
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
    @SuppressWarnings("unused")
    private void register(ActionEvent event) {
        if (usernameTextField.getText().isEmpty() || passwordField.getText().isEmpty() || emailTextField.getText().isEmpty()) {
            errorLabel.setText("Bạn chưa điền thông tin vào chỗ trống.");
        }
        else {
            String username = usernameTextField.getText();
            String email = emailTextField.getText();
            String password = passwordField.getText();

            UserDTO user = new UserDTO()
                            .setUsername(username)
                            .setEmail(email)
                            .setPassword(password);

            String response = ClientToServer.sendRequest("REGISTER", user);

            if (response.contains("SUCCESS")) {
                errorLabel.setText("Đăng ký thành công!");
            } else if (response.contains("PASSWORD LESS THAN 8")) {
                errorLabel.setText("Mật khẩu phải có từ 8 ký tự trở lên!");
            } else if (response.contains("DUPLICATE")) {
                errorLabel.setText("Tài khoản đã tồn tại.");
            }
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToLogin(ActionEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/loginregister/Login.fxml");
    }
}
