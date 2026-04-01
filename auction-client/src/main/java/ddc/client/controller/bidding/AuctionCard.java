package ddc.client.controller.bidding;

import ddc.client.model.AuctionItemViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class AuctionCard {

    @FXML
    private ImageView imgItem;

    @FXML
    private Label lblName;

    @FXML
    private Label lblPrice;

    @FXML
    private Label lblTimeLeft;

    public void setData(AuctionItemViewModel item) {
        lblName.setText(item.getName());
        lblPrice.setText(item.getPrice());
        lblTimeLeft.setText("◷ " + item.getTimeLeft());

        try {
            var imageStream = getClass().getResourceAsStream(item.getImagePath());

            if (imageStream == null) {
                System.out.println("Không tìm thấy ảnh: " + item.getImagePath());
                return;
            }

            Image image = new Image(imageStream);
            imgItem.setImage(image);

        } catch (Exception e) {
            System.out.println("Không load được ảnh: " + item.getImagePath());
            e.printStackTrace();
        }
    }
}