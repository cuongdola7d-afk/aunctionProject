package ddc.client.controller.home;
import ddc.client.controller.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class Home {

    @FXML
    private void switchToSelling(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/selling/Selling.fxml");
    }

    @FXML
    private void switchToBidding(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/bidding/Bidding.fxml");

    }
}