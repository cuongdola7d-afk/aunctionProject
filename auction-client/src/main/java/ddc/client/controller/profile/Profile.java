package ddc.client.controller.profile;

import java.io.IOException;

import ddc.client.controller.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;
public class Profile {
    @FXML
    private void switchToSelling (MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/selling/Selling.fxml");
    }

    @FXML
    private void switchToBidding (MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/bidding/Bidding.fxml");
    }

    @FXML
    private void switchToHome (MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/home/Home.fxml");
    }

    @FXML
    private void switchToPersonalInfo (MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/profile/Personalinfo.fxml");
    }
    @FXML
    void showLogoutPopup(MouseEvent event) {
        try {
            // 1. Chỉ nạp FXML, TUYỆT ĐỐI không dùng SceneSwitcher ở đây
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ddc/client/views/profile/logout.fxml"));
            Parent root = loader.load();

            // 2. Tạo một cửa sổ MỚI (Stage mới)
            Stage popupStage = new Stage();
            popupStage.setTitle("Xác nhận đăng xuất");
            popupStage.setResizable(false);

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
