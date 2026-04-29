package ddc.client.controller.profile;

import java.io.IOException;

import ddc.client.controller.SceneSwitcher;
import ddc.client.network.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
public class Profile {
    @FXML private Label nameLabel, usernameLabel;

    public void initialize(){
        UserSession session = UserSession.getInstance();

    // Đổ dữ liệu vào các ô TextField
        nameLabel.setText(session.getName());
        usernameLabel.setText("@" + session.getUsername());
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToSelling (MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/selling/Selling.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToBidding (MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/bidding/Bidding.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToHome (MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/home/Home.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToPersonalInfo (MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/profile/Personalinfo.fxml");
    }

    @FXML
    private void switchToNotify(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/notify/Notify.fxml");
    }

    @FXML
    private void switchToSecurity(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/profile/Security.fxml");
    }

    @FXML
    @SuppressWarnings({"unused", "CallToPrintStackTrace"})
    private void showLogoutPopup(MouseEvent event) {
        try {
            // 1. Chỉ nạp FXML, TUYỆT ĐỐI không dùng SceneSwitcher ở đây
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ddc/client/views/profile/logout.fxml"));
            Parent root = loader.load();

            // 2. Tạo một cửa sổ MỚI (Stage mới)
            Stage popupStage = new Stage();
            popupStage.setTitle("Xác nhận đăng xuất");
            popupStage.setResizable(false);
            
            Image icon = new Image(getClass().getResourceAsStream("/ddc/client/views/DDCAuction.png"));
            popupStage.getIcons().add(icon);

            // 3. Khóa màn hình chính (Profile) ở phía sau
            popupStage.initModality(Modality.APPLICATION_MODAL);
            
            // 4. Chỉ định "chủ sở hữu" là cửa sổ Profile hiện tại
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            popupStage.initOwner(primaryStage);

            // 5. Tạo Scene mới
            Scene scene = new Scene(root, 400, 300); 
            popupStage.setScene(scene);

            // 6. Hiển thị cửa sổ mới lên
            popupStage.centerOnScreen();
            popupStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
