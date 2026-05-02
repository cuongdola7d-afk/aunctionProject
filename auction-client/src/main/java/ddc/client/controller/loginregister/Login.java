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

public class Login {
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
                System.out.println("[" + Thread.currentThread().getName() + "] Dang gui request login...");
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
            loginTask.getException().printStackTrace();
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
                    .setPassword(user.getPassword());
            Platform.runLater(() -> errorLabel.setText("Đăng nhập thành công!"));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("IO Error!" + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error!" + e.getMessage());
            }
            
            Platform.runLater(() -> openHome(event));
            return;
        }

        Platform.runLater(() -> errorLabel.setText(loginErrorMessage(userRes == null ? null : userRes.getStatus())));
    }

    private String loginErrorMessage(String status) {
        return switch (status == null ? "" : status) {
            case "PASSWORD_LESS_THAN_8" -> "Mật khẩu phải có từ 8 ký tự trở lên.";
            case "UNAVAILABLE" -> "Tài khoản không tồn tại.";
            case "INVALID PASSWORD" -> "Mật khẩu không đúng.";
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
