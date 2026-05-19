package ddc.client.controller.profile;

import java.io.IOException;

import ddc.client.network.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logout {
    private static final Logger LOGGER = LoggerFactory.getLogger(Logout.class);

    @FXML
    private StackPane rootNode;

    @FXML
    void handleCancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleConfirmLogout(ActionEvent event) {
        try {
            // 1. Lấy Stage của cái Popup và đóng nó lại
            Stage popupStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 2. Lấy Stage "Chủ" (Chính là Stage đang hiện màn hình Profile)
            // Vì popup này được mở từ Profile, owner của nó chính là Stage Profile
            Stage primaryStage = (Stage) popupStage.getOwner();

            // 3. Đóng cái Popup trước
            popupStage.close();
            primaryStage.close();
            UserSession.getInstance().cleanUserSession();

            // 4. Tải giao diện Login vào Stage chính (primaryStage)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ddc/client/views/loginregister/Login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();

            Image icon = new Image(getClass().getResourceAsStream("/ddc/client/views/DDCAuction.png"));

            Scene scene = new Scene(root, 400, 500);

            stage.getIcons().add(icon);

            stage.setTitle("DDC Auction");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            LOGGER.error("Không tìm thấy file Login.fxml", e);
        }
    }
}
