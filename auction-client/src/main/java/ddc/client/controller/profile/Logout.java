package ddc.client.controller.profile;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            Stage popupStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage primaryStage = (Stage) popupStage.getOwner();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ddc/client/views/loginregister/Login.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            Image icon = new Image(getClass().getResourceAsStream("/ddc/client/views/DDCAuction.png"));
            Scene scene = new Scene(root, 400, 500);

            loginStage.getIcons().add(icon);
            loginStage.setTitle("DDC Auction");
            loginStage.setScene(scene);
            loginStage.setResizable(false);
            loginStage.show();

            popupStage.close();
            primaryStage.close();
            UserSession.getInstance().cleanUserSession();
            ddc.client.config.ClientContext.EXECUTOR.execute(() ->
                    ddc.client.network.client.GlobalSocketClient.getInstance().disconnect());
        } catch (IOException e) {
            LOGGER.error("Khong tim thay file login.fxml", e);
        }
    }
}
