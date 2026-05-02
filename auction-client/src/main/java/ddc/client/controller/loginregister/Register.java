package ddc.client.controller.loginregister;

import com.google.gson.Gson;

import ddc.client.config.ClientContext;
import ddc.client.config.GsonConfig;
import ddc.client.controller.SceneSwitcher;
import ddc.client.model.Request;
import ddc.client.model.UserDTO;
import ddc.client.network.RequestToServer;
import ddc.client.network.response.BaseResponse;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class Register {
    @FXML
    private TextField usernameTextField, emailTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;
    @FXML
    private Button registerButton;

    private final Gson gson = GsonConfig.newGson();

    @FXML
@SuppressWarnings("unused")
private void register(ActionEvent event) {
    String username = usernameTextField.getText() == null ? "" : usernameTextField.getText().trim();
    String email = emailTextField.getText() == null ? "" : emailTextField.getText().trim();
    String password = passwordField.getText() == null ? "" : passwordField.getText();

    if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
        errorLabel.setText("Bạn chưa điền đầy đủ thông tin.");
        return;
    }

    UserDTO user = new UserDTO()
            .setUsername(username)
            .setEmail(email)
            .setPassword(password);

    errorLabel.setText("Đang đăng ký...");
    registerButton.setDisable(true); // Khóa nút để tránh spam request

    // Tạo Task xử lý logic ngầm
    Task<String> registerTask = new Task<>() {
        @Override
        protected String call() throws Exception {
            System.out.println("[" + Thread.currentThread().getName() + "] Dang gui request dang ki cho: " + username);
            Request request = new Request().setAction("REGISTER").setData(user);
            return RequestToServer.sendRequest(request);
        }
    };

    // Xử lý khi đăng ký xong (Tự động quay về luồng UI)
    registerTask.setOnSucceeded(e -> {
        registerButton.setDisable(false);
        String response = registerTask.getValue();
        BaseResponse baseResponse = gson.fromJson(response, BaseResponse.class);
        String status = baseResponse == null ? null : baseResponse.getStatus();
        errorLabel.setText(registerMessage(status));
        
        // Nếu thành công, có thể tự động chuyển sang màn hình login hoặc thông báo
        if ("SUCCESS".equalsIgnoreCase(status)) {
            System.out.println("Dang ky thanh cong!");
        }
    });

    // Xử lý khi lỗi
    registerTask.setOnFailed(e -> {
        registerButton.setDisable(false);
        errorLabel.setText("Lỗi kết nối máy chủ khi đăng ký.");
        registerTask.getException().printStackTrace();
    });

    // Chạy bằng Executor dùng chung
    ClientContext.EXECUTOR.execute(registerTask);
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
