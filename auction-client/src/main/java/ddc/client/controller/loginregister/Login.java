package ddc.client.controller.loginregister;

import com.google.gson.Gson;

import ddc.client.config.GsonConfig;
import ddc.client.controller.SceneSwitcher;
import ddc.client.model.UserDTO;
import ddc.client.network.ClientToServer;
import ddc.client.network.UserSession;
import ddc.client.network.response.UserResponse;
import javafx.application.Platform;
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

    private final Gson gson = GsonConfig.newGson();

    @FXML
    private void login(ActionEvent event) {
        String username = usernameTextField.getText() == null ? "" : usernameTextField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        errorLabel.setText("Đang đăng nhập...");
        UserDTO user = new UserDTO()
                .setUsername(username)
                .setPassword(password);

        new Thread(() -> handleLoginResponse(event, ClientToServer.sendRequest("LOGIN", user))).start();
    }

    private void handleLoginResponse(ActionEvent event, String response) {
        UserResponse userRes = gson.fromJson(response, UserResponse.class);
        if (userRes != null && "SUCCESS".equals(userRes.getStatus()) && userRes.getData() != null) {
            UserDTO user = userRes.getData();
            UserSession.getInstance()
                    .setId(user.getId())
                    .setName(user.getName())
                    .setUsername(user.getUsername())
                    .setEmail(user.getEmail());

            Platform.runLater(() -> openHome(event));
            return;
        }

        Platform.runLater(() -> errorLabel.setText(loginErrorMessage(userRes == null ? null : userRes.getStatus())));
    }

    private String loginErrorMessage(String status) {
        return switch (status == null ? "" : status) {
            case "PASSWORD_LESS_THAN_8" -> "Mật khẩu phải có từ 8 ký tự trở lên.";
            case "INVALID_CREDENTIALS" -> "Tài khoản hoặc mật khẩu không đúng.";
            case "CONNECTION_ERROR" -> "Không kết nối được với server.";
            default -> "Đăng nhập thất bại.";
        };
    }

    private void openHome(ActionEvent event) {
        try {
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
            Parent root = FXMLLoader.load(getClass().getResource("/ddc/client/views/home/Home.fxml"));
            Stage stage = new Stage();
            Image icon = new Image(getClass().getResourceAsStream("/ddc/client/views/DDCAuction.png"));

            stage.setTitle("DDC Auction");
            stage.getIcons().add(icon);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (Exception e) {
            errorLabel.setText("Giao diện bị lỗi.");
        }
    }

    @FXML
    private void switchToRegister(ActionEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/loginregister/Register.fxml");
    }
}
