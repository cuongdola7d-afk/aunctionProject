package ddc.client.controller.profile;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import ddc.client.controller.SceneSwitcher;
import ddc.client.network.UserSession;
import javafx.event.ActionEvent;


public class Personalinfo {
@FXML private TextField txtUsername, txtEmail, txtPhone, txtId, txtName;
@FXML private Button btnSave;

@FXML
public void initialize() {
    
    // Lấy dữ liệu từ Session
    UserSession session = UserSession.getInstance();

    // Đổ dữ liệu vào các ô TextField
    txtName.setText(session.getName());
    txtUsername.setText("@" + session.getUsername());
    txtEmail.setText(session.getEmail());
    txtPhone.setText(session.getPhone());
    txtId.setText(session.getId());
    
    // Vô hiệu hóa chỉnh sửa cho UserID (vì thường ID không được sửa)
    txtId.setEditable(false);
    
    
    // Tạo một hàm kiểm tra sự thay đổi
    Runnable checkChanges = () -> {
        btnSave.setDisable(false);
        btnSave.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white; -fx-background-radius: 10;");
    };

    txtUsername.textProperty().addListener((observable, oldValue, newValue) -> checkChanges.run());
    txtEmail.textProperty().addListener((observable, oldValue, newValue) -> checkChanges.run());
    txtPhone.textProperty().addListener((observable, oldValue, newValue) -> checkChanges.run());
}


@FXML
void handleSaveProfile(ActionEvent event) {
    btnSave.setDisable(true);
    btnSave.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: white; -fx-background-radius: 10;");
}

@FXML
private void switchBackToProfile(MouseEvent event){
    SceneSwitcher.goTo(event, "/ddc/client/views/profile/Profile.fxml");
}
}