package ddc.client.controller.bidding;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddc.client.model.AuctionItemViewModel;
import ddc.client.model.AuctionStatus;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AuctionCard {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuctionCard.class);

    @FXML
    private HBox cardRoot;

    @FXML
    private ImageView imgItem;

    @FXML
    private Label lblName;

    @FXML
    private Label lblPrice;

    @FXML
    private Label lblTimeLeft;

    @FXML
    private Label lblCategory;

    private AuctionItemViewModel item;
    private String currentBidderId;
    private String currentloadedUrl;

    public void setData(AuctionItemViewModel item, String currentBidderId) {
        this.item = item;
        this.currentBidderId = currentBidderId;

        lblName.setText(item.getName());
        lblPrice.setText(item.getPrice());

        lblCategory.setText(item.getCategory());
        updateStatusLabel(item);

        String newImagePath = item.getImagePath();

        if (newImagePath != null && !newImagePath.equals(currentloadedUrl)) {
            currentloadedUrl = newImagePath;

            try {
                var imageUrl = item.getImagePath().startsWith("http://") || item.getImagePath().startsWith("https://")
                        ? null
                        : getClass().getResource(item.getImagePath());
                if (imageUrl != null) {
                    imgItem.setImage(new Image(imageUrl.toExternalForm()));
                } else if (item.getImagePath().startsWith("http://") || item.getImagePath().startsWith("https://")) {
                    imgItem.setImage(new Image(newImagePath, true));
                } else {
                    LOGGER.warn("Không tìm thấy ảnh tại: {}", item.getImagePath());
                }
            } catch (Exception e) {
                LOGGER.warn("Lỗi khi load ảnh: {}", item.getImagePath());
            }
        }
    }

    private void updateStatusLabel(AuctionItemViewModel item) {
        lblTimeLeft.textProperty().unbind();
        if (item.getStatus() == AuctionStatus.CANCELLED) {
            lblTimeLeft.setText("◷: Phiên đấu giá đã bị huỷ");
            lblTimeLeft.setStyle("-fx-font-size: 13px; -fx-text-fill: #334155;");
            return;
        }

        lblTimeLeft.textProperty().bind(Bindings.concat("◷: ", item.timeLeftProperty()));
        lblTimeLeft.setStyle("-fx-font-size: 13px; -fx-text-fill: #334155;");
    }

    @FXML
    private void handleCardClick(MouseEvent event) {
        if (item == null) {
            LOGGER.warn("Item chưa được gán.");
            return;
        }

        if (item.getAuctionId() == null || item.getAuctionId().isBlank()) {
            LOGGER.warn("Thiếu auctionId.");
            return;
        }

        if (currentBidderId == null || currentBidderId.isBlank()) {
            LOGGER.warn("Thiếu bidderId.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ddc/client/views/bidding/auction-detail.fxml"));
            Parent root = loader.load();

            AuctionDetail controller = loader.getController();
            controller.setProductData(
                    item.getName(),
                    item.getPrice(),
                    item.getImagePath(),
                    item);

            Stage stage = new Stage();
            stage.setTitle(item.getName());
            stage.centerOnScreen();

            Image icon = new Image(getClass().getResourceAsStream("/ddc/client/views/DDCAuction.png"));
            stage.getIcons().add(icon);

            Stage ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.initOwner(ownerStage);

            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.show();

            Platform.runLater(() -> controller.setupAuctionContext(item.getAuctionId(), currentBidderId));

        } catch (IOException e) {
            LOGGER.error("Không mở được trang chi tiết", e);

        }
    }
}
