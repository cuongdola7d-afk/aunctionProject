package ddc.client.controller.profile;

import com.google.gson.Gson;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import ddc.client.config.ClientContext;
import ddc.client.controller.SceneSwitcher;
import ddc.client.model.Request;
import ddc.client.model.UserDTO;
import ddc.client.network.RequestToServer;
import ddc.client.network.UserSession;
import ddc.client.network.response.BaseResponse;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;


public class Personalinfo {
    @FXML private TextField txtUsername, txtEmail, txtId, txtName;
    @FXML private Button btnSave;
    @FXML private Label lblErrorEmail;

    @FXML
    public void initialize() {
        
        // Lấy dữ liệu từ Session
        UserSession session = UserSession.getInstance();

        // Đổ dữ liệu vào các ô TextField
        txtName.setText(session.getName());
        txtUsername.setText("@" + session.getUsername());
        txtEmail.setText(session.getEmail());
        txtId.setText(session.getId());
        
        txtUsername.setEditable(false);
        txtId.setEditable(false);
        
        
        // Tạo một hàm kiểm tra sự thay đổi
        Runnable checkChanges = () -> {
            btnSave.setDisable(false);
            btnSave.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white; -fx-background-radius: 10;");
        };

        txtName.textProperty().addListener((observable, oldValue, newValue) -> checkChanges.run());
        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> checkChanges.run());

        lblErrorEmail.managedProperty().bind(lblErrorEmail.visibleProperty());
        lblErrorEmail.setVisible(false);

        // Lắng nghe khi người dùng gõ vào ô Email
        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            if (lblErrorEmail.isVisible()) {
                lblErrorEmail.setVisible(false);
                txtEmail.setStyle("-fx-background-color: transparent;"); 
            }
        });
    }


    @FXML
    private void handleSaveProfile(ActionEvent event) {
        // 1. Lấy dữ liệu từ UI
        String id = txtId.getText().trim();
        String newName = txtName.getText().trim();
        String newEmail = txtEmail.getText().trim();

        if (newName.isEmpty() || newEmail.isEmpty()) {
            showAlert("Lỗi", "Vui lòng điền đầy đủ thông tin!");
            return;
        }

        if (!isValidEmail(newEmail)) {
            lblErrorEmail.setText("Email không đúng định dạng!");
            lblErrorEmail.setVisible(true);
            txtEmail.setStyle("-fx-border-color: red;");
            return;
        } else {
            lblErrorEmail.setVisible(false);
            txtEmail.setStyle("-fx-background-color: transparent;");
        }

        // 2. Hiện trạng thái đang xử lý
        btnSave.setDisable(true);
        btnSave.setText("Đang lưu thay đổi...");

        // 3. Tạo Task xử lý ngầm
        Task<String> updateTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                // Đóng gói request
                UserDTO user = new UserDTO().setId(id).setEmail(newEmail).setName(newName);
                Request request = new Request()
                        .setAction("UPDATE_PROFILE")
                        .setData(user);

                // Gửi qua RequestToServer (Sử dụng EXECUTOR ngầm định)
                return RequestToServer.sendRequest(request);
            }
        };

        // 4. Xử lý kết quả trả về (Tự động về luồng UI)
        updateTask.setOnSucceeded(e -> {
            btnSave.setDisable(false);
            String jsonResponse = updateTask.getValue();
            Gson gson = new Gson();
            BaseResponse response = gson.fromJson(jsonResponse, BaseResponse.class);

            if ("SUCCESS".equals(response.getStatus())) {
                btnSave.setText("Cập nhật thành công!");
                resetSaveButton();
                UserSession.getInstance().setName(newName).setEmail(newEmail);
            } else {
                btnSave.setText("Lỗi: " + response.getMessage());
                resetSaveButton();
            }
        });

        updateTask.setOnFailed(e -> {
            resetSaveButton();
        });

        // 5. Thực thi bằng luồng ảo
        ClientContext.EXECUTOR.execute(updateTask);
    }

    private void showAlert(String title, String content) {
        // Chạy trên luồng UI để đảm bảo không bị crash
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null); // Để null cho gọn, không có dòng tiêu đề phụ
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    private void resetSaveButton() {
        Platform.runLater(() -> {
            btnSave.setDisable(true);
            btnSave.setText("Lưu");
            btnSave.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white; -fx-background-radius: 10;");
        });
    }

    private boolean isValidEmail(String email) {
        // Regex chuẩn để kiểm tra email
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    @FXML
    private void switchBackToProfile(MouseEvent event){
        SceneSwitcher.goTo(event, "/ddc/client/views/profile/Profile.fxml");
    }
}