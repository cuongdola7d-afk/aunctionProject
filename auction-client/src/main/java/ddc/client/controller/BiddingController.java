package ddc.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class BiddingController {

    @FXML
    private Label lblItemName;

    @FXML
    private Label lblCurrentPrice;

    @FXML
    private Label lblHighestBidder;

    @FXML
    private Label lblTimeLeft;

    @FXML
    private Label lblMessage;

    @FXML
    private TextField txtBidAmount;

    @FXML
    private TableView<?> tableBidHistory;

    @FXML
    private TableColumn<?, ?> colBidder;

    @FXML
    private TableColumn<?, ?> colAmount;

    @FXML
    private TableColumn<?, ?> colTime;

    @FXML
    public void initialize() {
        lblItemName.setText("Laptop Gaming ASUS ROG");
        lblCurrentPrice.setText("15,000,000 VND");
        lblHighestBidder.setText("duki123");
        lblTimeLeft.setText("00:25:18");
        lblMessage.setText("Chào mừng đến phiên đấu giá");
    }

    @FXML
    private void handlePlaceBid() {
        String bidText = txtBidAmount.getText();
        if (bidText == null || bidText.trim().isEmpty()) {
            lblMessage.setText("Vui lòng nhập giá bid");
            return;
        }

        lblMessage.setText("Đã gửi giá bid: " + bidText);
        txtBidAmount.clear();
    }
}