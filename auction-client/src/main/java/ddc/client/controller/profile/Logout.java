package ddc.client.controller.profile;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddc.client.network.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

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
        new Thread(() -> {
            try {
                UserSession.getInstance().cleanUserSession();
                ddc.client.network.client.GlobalSocketClient.getInstance().disconnect();
                Platform.runLater(() -> {
                    try {
                        Stage popupStage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                        Stage primaryStage = (Stage) popupStage.getOwner();

                        popupStage.close();
                        primaryStage.close();

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
                });
            } catch (Exception e) {
                LOGGER.error("co loi xay ra trong qua trinh ngat ket noi mang", e);
                e.printStackTrace();
            }
        }).start();
        
    }
}
