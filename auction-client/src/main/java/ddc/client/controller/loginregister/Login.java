package ddc.client.controller.loginregister;

import com.google.gson.Gson;

import ddc.client.config.ClientContext;
import ddc.client.config.GsonConfig;
import ddc.client.controller.SceneSwitcher;
import ddc.client.model.Request;
import ddc.client.model.UserDTO;
import ddc.client.network.RequestToServer;
import ddc.client.network.UserSession;
import ddc.client.network.response.UserResponse;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Login {

    private static final Logger LOGGER = LoggerFactory.getLogger(Login.class);

    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button loginButton;

    private final Gson gson = GsonConfig.newGson();

    @FXML
    private void login(ActionEvent event) {
        String username = usernameTextField.getText() == null ? "" : usernameTextField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        // 1. Cập nhật trạng thái UI ban đầu
        errorLabel.setText("Đang đăng nhập...");
        loginButton.setDisable(true); // Nên disable nút để tránh bấm nhiều lần

        UserDTO user = new UserDTO().setUsername(username).setPassword(password);
        Request loginRequest = new Request().setAction("LOGIN").setData(user);

        // 2. Tạo Task để xử lý đăng nhập ngầm
        Task<String> loginTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                // In để kiểm tra luồng ảo của Java 25
                LOGGER.info("Dang gui request login...");
                return RequestToServer.sendRequest(loginRequest);
            }
        };

        // 3. Xử lý khi đăng nhập xong (Tự động quay về UI Thread)
        loginTask.setOnSucceeded(e -> {
            loginButton.setDisable(false);
            String response = loginTask.getValue();
            handleLoginResponse(event, response);
        });

        // 4. Xử lý khi có lỗi
        loginTask.setOnFailed(e -> {
            loginButton.setDisable(false);
            errorLabel.setText("Lỗi kết nối đến máy chủ.");
            LOGGER.error("Loi ket noi dang nhap", loginTask.getException());
        });

        // 5. Giao cho Executor xử lý thay vì tạo Thread mới
        ClientContext.EXECUTOR.execute(loginTask);
    }

    private void handleLoginResponse(ActionEvent event, String response) {
        UserResponse userRes = gson.fromJson(response, UserResponse.class);
        if (userRes != null && "SUCCESS".equals(userRes.getStatus()) && userRes.getData() != null) {
            UserDTO user = userRes.getData();
            UserSession.getInstance()
                    .setId(user.getId())
                    .setName(user.getName())
                    .setUsername(user.getUsername())
                    .setEmail(user.getEmail())
                    .setPassword(user.getPassword())
                    .setRole(user.getRole())
                    .setStatus(user.getStatus());
            Platform.runLater(() -> errorLabel.setText("Đăng nhập thành công!"));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOGGER.warn("IO Error: {}", e.getMessage());
            } catch (Exception e) {
                LOGGER.error("Login error: {}", e.getMessage());
            }

            Platform.runLater(() -> openMainScreen(event, user));
            return;
        }

        Platform.runLater(() -> errorLabel.setText(loginErrorMessage(userRes == null ? null : userRes.getStatus())));
    }

    private String loginErrorMessage(String status) {
        return switch (status == null ? "" : status) {
            case "PASSWORD_LESS_THAN_8" -> "Mật khẩu phải có từ 8 ký tự trở lên.";
            case "UNAVAILABLE" -> "Tài khoản không tồn tại.";
            case "INVALID PASSWORD" -> "Mật khẩu không đúng.";
            case "BLOCKED" -> "Tai khoan da bi khoa.";
            case "CONNECTION_ERROR" -> "Không kết nối được với server.";
            default -> "Đăng nhập thất bại.";
        };
    }

    private void openMainScreen(ActionEvent event, UserDTO user) {
        try {
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
            boolean isAdmin = "ADMIN".equalsIgnoreCase(user.getRole()) || "admin".equalsIgnoreCase(user.getUsername());
            String fxml = isAdmin ? "/ddc/client/views/admin/AdminDashboard.fxml" : "/ddc/client/views/home/Home.fxml";
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = new Stage();
            Image icon = new Image(getClass().getResourceAsStream("/ddc/client/views/DDCAuction.png"));

            stage.setTitle(isAdmin ? "DDC Auction Admin" : "DDC Auction");
            stage.getIcons().add(icon);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.setScene(new Scene(root, isAdmin ? 980 : 800, isAdmin ? 680 : 600));
            stage.show();
        } catch (Exception e) {
            LOGGER.error("Loi mo giao dien sau login", e);
            errorLabel.setText("Giao diện bị lỗi.");
        }
    }

    @FXML
    private void switchToRegister(ActionEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/loginregister/Register.fxml");
    }
}
