package ddc.client.controller.profile;

import com.google.gson.Gson;

import ddc.client.Client;
import ddc.client.controller.SceneSwitcher;
import ddc.client.model.UserDTO;
import ddc.client.network.ClientToServer;
import ddc.client.network.UserSession;
import ddc.client.network.response.BaseResponse;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.MouseEvent;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Security {
    @FXML private PasswordField txtCurrentPassword;
    @FXML private PasswordField txtNewPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private Label errorLabel;
    @FXML private Button btnChangePassword;
    
    @FXML
    private void switchBackToProfile(MouseEvent event){
        SceneSwitcher.goTo(event, "/ddc/client/views/profile/Profile.fxml");
    }

    @FXML
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    @FXML
    private void handleChangePassword() {
        // 0. Xóa thông báo lỗi cũ mỗi khi nhấn nút
        errorLabel.setText(""); 
        
        String currentPass = txtCurrentPassword.getText();
        String newPass = txtNewPassword.getText();
        String confirmPass = txtConfirmPassword.getText();

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showError("Vui lòng điền đầy đủ các trường!");
            return;
        }

        // 3. So sánh mật khẩu hiện tại với UserSession
        String passwordInSession = UserSession.getInstance().getPassword(); 
        
        if (!currentPass.equals(passwordInSession)) {
            showError("Mật khẩu hiện tại không chính xác!");
            // Bạn có thể làm nổi bật ô nhập bằng viền đỏ
            txtCurrentPassword.setStyle("-fx-border-color: red;");
            return;
        } else {
            txtCurrentPassword.setStyle(""); // Xóa viền đỏ nếu đã nhập đúng
        }

        // 1. Kiểm tra trống
        if (newPass.isEmpty()) {
            showError("Mật khẩu mới không được để trống!");
            return;
        }

        // 2. Kiểm tra khớp
        if (!newPass.equals(confirmPass)) {
            showError("Mật khẩu xác nhận không trùng khớp!");
            return;
        }

        if (newPass.length() < 8) {
            showError("Mật khẩu mới phải có độ dài hơn 8!");
            return;
        }

        if (newPass.equals(currentPass)){
            showError("Mật khẩU mới không được trùng với mật khẩu cũ");
            return;
        }


        String username = UserSession.getInstance().getUsername();
        // Tạo một đối tượng UserDTO chỉ chứa username và mật khẩu mới
        UserDTO user = new UserDTO()
                    .setUsername(username)
                    .setPassword(newPass); 

        // 1. Gửi request
        String jsonResponse = ClientToServer.sendRequest("UPDATE_PASSWORD", user);

        // 2. Parse kết quả trả về từ Server
        Gson gson = new Gson();
        // Giả sử bạn dùng chung lớp BaseResponse ở cả 2 bên
        BaseResponse res = gson.fromJson(jsonResponse, BaseResponse.class);

        // 3. Kiểm tra status
        if (res != null && "SUCCESS".equalsIgnoreCase(res.getStatus())) {
            // Cập nhật mật khẩu mới vào Session
            UserSession.getInstance().setPassword(newPass);
            
            showSuccessEffect();

            // Xóa trắng các ô nhập
            txtCurrentPassword.clear();
            txtNewPassword.clear();
            txtConfirmPassword.clear();
        } else {
            showError("Đổi mật khẩu thất bại hoặc mật khẩu cũ không đúng!");
        }
    }


    private void showSuccessEffect() {
        String originalText = btnChangePassword.getText();
        String originalStyle = btnChangePassword.getStyle();

        // 2. Thiết lập trạng thái "Thành công" (Xanh lá + Dấu tick)
        btnChangePassword.setText("✔ Thành công");
        btnChangePassword.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        btnChangePassword.setDisable(true); // Vô hiệu hóa tạm thời để tránh bấm liên tục

        // 3. Tạo hiệu ứng chờ 2 giây rồi quay lại ban đầu
        Timeline timeline = new Timeline(new KeyFrame(
            Duration.seconds(2), 
            ae -> {
                btnChangePassword.setText(originalText);
                btnChangePassword.setStyle(originalStyle);
                btnChangePassword.setDisable(false);
            }
        ));
        timeline.play();
    }
}

