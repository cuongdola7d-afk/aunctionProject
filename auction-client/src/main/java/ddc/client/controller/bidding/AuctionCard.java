package ddc.client.controller.bidding;

import ddc.client.controller.SceneSwitcher;
import ddc.client.model.AuctionItemViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox; // Phải là HBox

public class AuctionCard {

    @FXML
    private HBox cardRoot; // Khớp với thẻ <HBox> trong FXML

    @FXML
    private ImageView imgItem;

    @FXML
    private Label lblName;

    @FXML
    private Label lblPrice;

    @FXML
    private Label lblTimeLeft;

    @FXML
    private Label lblCategory; //

    private AuctionItemViewModel item;

public void setData(AuctionItemViewModel item) {
    this.item = item;
    lblName.setText(item.getName());
    lblPrice.setText(item.getPrice());
    lblTimeLeft.setText("◷ " + item.getTimeLeft());
    lblCategory.setText(item.getCategory());

    try {
        // Sử dụng getResource thay vì getResourceAsStream để dễ kiểm tra URL
        var imageUrl = getClass().getResource(item.getImagePath());
        if (imageUrl != null) {
            imgItem.setImage(new Image(imageUrl.toExternalForm()));
        } else {
            // Dòng này sẽ in ra Terminal nếu đường dẫn bị sai
            System.err.println("LỖI: Không tìm thấy ảnh tại: " + item.getImagePath());
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    @FXML
    private void handleCardClick(MouseEvent event) {
        // Gọi SceneSwitcher để chuyển trang
        //System.out.println("Đã click vào sản phẩm!");
        SceneSwitcher.goTo(event, "/ddc/client/views/bidding/auction-detail.fxml");
    }
}