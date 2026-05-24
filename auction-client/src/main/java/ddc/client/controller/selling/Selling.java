package ddc.client.controller.selling;

import java.io.IOException;

import ddc.client.controller.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.Label;
import ddc.client.controller.notify.NotificationBadgeUtil;

public class Selling {

    @FXML
    private Label badgeLabel;

    @FXML
    public void initialize() {
        NotificationBadgeUtil.setupBadge(badgeLabel);
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(Selling.class);

    @FXML
    @SuppressWarnings("unused")
    private void handleOpenUploadDialog(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ddc/client/views/selling/UploadItem.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Tạo mục đấu giá mới");
            stage.setResizable(false);
            stage.centerOnScreen();

            Image icon = new Image(getClass().getResourceAsStream("/ddc/client/views/DDCAuction.png"));
            stage.getIcons().add(icon);

            Stage ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.initOwner(ownerStage);

            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            LOGGER.error("Loi mo dialog upload", e);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToHome(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/home/Home.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToBidding(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/bidding/Bidding.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToProfile(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/profile/Profile.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToNotify(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/notify/Notify.fxml");
    }
}