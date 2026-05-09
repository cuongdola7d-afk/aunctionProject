package ddc.client.controller.bidding;

import java.io.IOException;

import ddc.client.model.AuctionItemViewModel;
import javafx.application.Platform;
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

    public void setData(AuctionItemViewModel item, String currentBidderId) {
        this.item = item;
        this.currentBidderId = currentBidderId;

        lblName.setText(item.getName());
        lblPrice.setText(item.getPrice());
        lblTimeLeft.setText("◷ " + item.getTimeLeft());
        lblCategory.setText(item.getCategory());

        try {
            var imageUrl = getClass().getResource(item.getImagePath());
            if (imageUrl != null) {
                imgItem.setImage(new Image(imageUrl.toExternalForm()));
            } else {
                System.err.println("LỖI: Không tìm thấy ảnh tại: " + item.getImagePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCardClick(MouseEvent event) {
        if (item == null) {
            System.out.println("Item chưa được gán.");
            return;
        }

        if (item.getAuctionId() == null || item.getAuctionId().isBlank()) {
            System.out.println("Thiếu auctionId.");
            return;
        }

        if (currentBidderId == null || currentBidderId.isBlank()) {
            System.out.println("Thiếu bidderId hiện tại.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ddc/client/views/bidding/auction-detail.fxml"));
            Parent root = loader.load();

            AuctionDetail controller = loader.getController();
            controller.setProductData(
                    item.getName(),
                    item.getPrice(),
                    item.getImagePath()
            );

            Stage stage = new Stage();
            stage.setTitle(item.getName());
            stage.centerOnScreen();

            Image icon = new Image(getClass().getResourceAsStream("/ddc/client/views/DDCAuction.png"));
            stage.getIcons().add(icon);

            Stage ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.initOwner(ownerStage);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();

            Platform.runLater(() ->
                    controller.setupAuctionContext(item.getAuctionId(), currentBidderId));

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Không mở được trang chi tiết đấu giá.");
        }
    }
}