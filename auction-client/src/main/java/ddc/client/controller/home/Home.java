package ddc.client.controller.home;
import ddc.client.controller.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import ddc.client.controller.notify.NotificationBadgeUtil;

public class Home {

    @FXML
    private Label badgeLabel;

    @FXML
    public void initialize() {
        NotificationBadgeUtil.setupBadge(badgeLabel);
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
    private void switchToProfile (MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/profile/Profile.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToNotify (MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/notify/Notify.fxml");
    }
    
    @FXML
    @SuppressWarnings("unused")
    private void learnMore (ActionEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/bidding/Bidding.fxml");
    }
}
