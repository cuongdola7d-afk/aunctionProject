package ddc.client.controller.profile;

import com.google.gson.Gson;

import ddc.client.config.ClientContext;
import ddc.client.controller.SceneSwitcher;
import ddc.client.model.Request;
import ddc.client.model.UserDTO;
import ddc.client.network.RequestToServer;
import ddc.client.network.UserSession;
import ddc.client.network.response.BaseResponse;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class Security {
    @FXML private PasswordField txtCurrentPassword;
    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Label errorLabel;
    @FXML private Button btnChangePassword;

    @FXML
    private void switchBackToProfile(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/profile/Profile.fxml");
    }

    @FXML
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    @FXML
    private void handleChangePassword() {
        // 0. Xóa thông báo cũ
        errorLabel.setText("");
        txtCurrentPassword.setStyle("");

        String currentPass = txtCurrentPassword.getText();
        String newPass = txtNewPassword.getText();
        String confirmPass = txtConfirmPassword.getText();

        // --- BƯỚC 1: VALIDATION TẠI CLIENT
        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showError("Vui lòng điền đầy đủ các trường!");
            return;
        }

        String passwordInSession = UserSession.getInstance().getPassword();
        if (!currentPass.equals(passwordInSession)) {
            showError("Mật khẩu hiện tại không chính xác!");
            txtCurrentPassword.setStyle("-fx-border-color: red;");
            return;
        }

        if (newPass.length() < 8) {
            showError("Mật khẩu mới phải có độ dài từ 8 ký tự trở lên!");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showError("Mật khẩu xác nhận không trùng khớp!");
            return;
        }

        if (newPass.equals(currentPass)) {
            showError("Mật khẩu mới không được trùng với mật khẩu cũ!");
            return;
        }

        // --- BƯỚC 2: CHUẨN BỊ DỮ LIỆU ---
        String username = UserSession.getInstance().getUsername();
        UserDTO user = new UserDTO().setUsername(username).setPassword(newPass);

        // --- BƯỚC 3: SỬ DỤNG TASK ĐỂ GỬI SOCKET CHẠY NGẦM ---
        
        btnChangePassword.setDisable(true);
        errorLabel.setText("Đang xử lý..."); 
        errorLabel.setStyle("-fx-text-fill: #3498db;");

        Task<String> changePasswordTask = new Task<>() {
            Request rq = new Request("UPDATE_PASSWORD", user);
            @Override
            protected String call() throws Exception {
                return RequestToServer.sendRequest(rq);
            }
        };

        // Xử lý khi Server phản hồi thành công
        changePasswordTask.setOnSucceeded(e -> {
            btnChangePassword.setDisable(false);
            
            String jsonResponse = changePasswordTask.getValue();
            Gson gson = new Gson();
            BaseResponse res = gson.fromJson(jsonResponse, BaseResponse.class);

            if (res != null && "SUCCESS".equalsIgnoreCase(res.getStatus())) {
                UserSession.getInstance().setPassword(newPass);
                showSuccessEffect();
                txtCurrentPassword.clear();
                txtNewPassword.clear();
                txtConfirmPassword.clear();
            } else {
                showError("Server khong phan hoi...");
            }
        });

        // Xử lý khi có lỗi
        changePasswordTask.setOnFailed(e -> {
            btnChangePassword.setDisable(false);
            errorLabel.setStyle("-fx-text-fill: red;");
            showError("Khong the ket noi toi may chu!");
            changePasswordTask.getException().printStackTrace();
        });

        // Kích hoạt luồng phụ chạy
        ClientContext.EXECUTOR.execute(changePasswordTask);
    }

    private void showSuccessEffect() {
        String originalText = btnChangePassword.getText();
        String originalStyle = btnChangePassword.getStyle();

        btnChangePassword.setText("✔ Thành công");
        btnChangePassword.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        btnChangePassword.setDisable(true);

        Timeline timeline = new Timeline(new KeyFrame(
            Duration.seconds(2),
            ae -> {
                btnChangePassword.setText(originalText);
                btnChangePassword.setStyle(originalStyle);
                btnChangePassword.setDisable(false);
                errorLabel.setText("");
            }
        ));
        timeline.play();
    }
}