package ddc.client.controller.profile;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class Wallet {
    @FXML
    private Label balanceLabel;

    public void initialize() {
        balanceLabel.setText(formatBalance(0));
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleDeposit(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ví của tôi");
        alert.setHeaderText(null);
        alert.setContentText("Chức năng nạp tiền đang được phát triển.");
        alert.showAndWait();
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private String formatBalance(double balance) {
        return String.format("%,.0f VND", balance);
    }
}
