package ddc.client.controller.bidding;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;

public class AuctionDetail {

    @FXML 
    private Label lblStartTime;

    @FXML 
    private Label lblEndTime;

    @FXML
    private ImageView mainImage;

    @FXML
    private Label lblPrice;

    @FXML
    private FlowPane thumbnailContainer;

    @FXML
    public void initialize() {
        // Bạn sẽ nhận dữ liệu từ AuctionCard truyền sang tại đây
        // Tạm thời set cứng dữ liệu để test giao diện
    }
    
    // Hàm này dùng để cập nhật thông tin sản phẩm
    public void setProductData(String name, String price, String imagePath) {
        lblPrice.setText(price);
        try {
            mainImage.setImage(new Image(getClass().getResourceAsStream(imagePath)));
        } catch (Exception e) {
            System.out.println("Lỗi load ảnh chi tiết");
        }
    }
}