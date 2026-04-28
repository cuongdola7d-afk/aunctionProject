package ddc.client.controller.loginregister;

import com.google.gson.Gson;

import ddc.client.config.GsonConfig;
import ddc.client.controller.SceneSwitcher;
import ddc.client.model.UserDTO;
import ddc.client.network.ClientToServer;
import ddc.client.network.response.BaseResponse;
import javafx.application.Platform;
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

    private final Gson gson = GsonConfig.newGson();

    @FXML
    @SuppressWarnings("unused")
    private void register(ActionEvent event) {
        String username = usernameTextField.getText() == null ? "" : usernameTextField.getText().trim();
        String email = emailTextField.getText() == null ? "" : emailTextField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            errorLabel.setText("Ban chua dien day du thong tin.");
            return;
        }

        UserDTO user = new UserDTO()
                .setUsername(username)
                .setEmail(email)
                .setPassword(password);

        errorLabel.setText("Dang dang ky...");
        new Thread(() -> {
            String response = ClientToServer.sendRequest("REGISTER", user);
            BaseResponse baseResponse = gson.fromJson(response, BaseResponse.class);
            String status = baseResponse == null ? null : baseResponse.getStatus();
            Platform.runLater(() -> errorLabel.setText(registerMessage(status)));
        }).start();
    }

    private String registerMessage(String status) {
        return switch (status == null ? "" : status) {
            case "SUCCESS" -> "Dang ky thanh cong.";
            case "PASSWORD_LESS_THAN_8" -> "Mat khau phai co tu 8 ky tu tro len.";
            case "INVALID_EMAIL" -> "Email khong hop le.";
            case "DUPLICATE" -> "Tai khoan da ton tai.";
            case "CONNECTION_ERROR" -> "Khong ket noi duoc server.";
            default -> "Dang ky that bai.";
        };
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToLogin(ActionEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/loginregister/Login.fxml");
    }
}
