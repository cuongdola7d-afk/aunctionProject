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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.control.Label;
import ddc.client.controller.notify.NotificationBadgeUtil;

public class Profile {

    @FXML
    private Label badgeLabel;

    private static final Logger LOGGER = LoggerFactory.getLogger(Profile.class);
    private static final double WALLET_POPUP_WIDTH = 320;
    private static final double WALLET_POPUP_HEIGHT = 320;

    @FXML
    private Label nameLabel, usernameLabel;

    @FXML
    public void initialize() {
        NotificationBadgeUtil.setupBadge(badgeLabel);
        UserSession session = UserSession.getInstance();
        nameLabel.setText(session.getName());
        usernameLabel.setText("@" + session.getUsername());
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToSelling(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/selling/Selling.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToBidding(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/bidding/Bidding.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToHome(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/home/Home.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToPersonalInfo(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/profile/Personalinfo.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToNotify(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/notify/Notify.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToSecurity(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/profile/Security.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void showWallet(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ddc/client/views/profile/Wallet.fxml"));
            Parent root = loader.load();

            Stage owner = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage walletStage = new Stage();
            walletStage.setTitle("Ví của tôi");
            walletStage.setResizable(false);
            walletStage.initModality(Modality.APPLICATION_MODAL);
            walletStage.initOwner(owner);
            walletStage.setScene(new Scene(root, WALLET_POPUP_WIDTH, WALLET_POPUP_HEIGHT));
            walletStage.setX(owner.getX() + (owner.getWidth() - WALLET_POPUP_WIDTH) / 2);
            walletStage.setY(owner.getY() + (owner.getHeight() - WALLET_POPUP_HEIGHT) / 2);
            walletStage.show();
        } catch (IOException e) {
            LOGGER.error("Loi hien thi popup wallet", e);
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void showLogoutPopup(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ddc/client/views/profile/Logout.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Xác nhận đăng xuất");
            popupStage.setResizable(false);

            Image icon = new Image(getClass().getResourceAsStream("/ddc/client/views/DDCAuction.png"));
            popupStage.getIcons().add(icon);
            popupStage.initModality(Modality.APPLICATION_MODAL);

            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            popupStage.initOwner(primaryStage);

            Scene scene = new Scene(root, 400, 300);
            popupStage.setScene(scene);
            popupStage.centerOnScreen();
            popupStage.show();
        } catch (IOException e) {
            LOGGER.error("Loi hien thi popup logout", e);
        }
    }
}
