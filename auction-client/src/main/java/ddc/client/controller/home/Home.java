package ddc.client.controller.home;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ddc.client.config.GsonConfig;
import ddc.client.controller.SceneSwitcher;
import ddc.client.controller.bidding.AuctionDetail;
import ddc.client.controller.notify.NotificationBadgeUtil;
import ddc.client.model.AuctionDTO;
import ddc.client.model.AuctionItemViewModel;
import ddc.client.model.Request;
import ddc.client.network.RealtimeToServer;
import ddc.client.network.UserSession;
import ddc.client.network.response.GetAllAuctionsResponse;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Home {

    @FXML
    private Label badgeLabel;

    @FXML
    private ImageView mostBidsImageView, highestPriceImageView, recentlyEndedImageView;

    @FXML
    private Label mostBidsNameLabel, mostBidsTypeLabel, mostBidsPriceLabel, mostBidsStatusLabel,
                  highestPriceNameLabel, highestPriceTypeLabel, highestPricePriceLabel, highestPriceStatusLabel,
                  recentlyEndedNameLabel, recentlyEndedTypeLabel, recentlyEndedPriceLabel, recentlyEndedStatusLabel;

    @FXML
    private VBox mostBidsVBox, highestPriceVBox, recentlyEndedVBox;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Home.class);
    private static final Gson gson = GsonConfig.newGson();

    @FXML
    public void initialize() {
        NotificationBadgeUtil.setupBadge(badgeLabel);
        loadHotAuctions();
    }

    private void loadHotAuctions () {
        new Thread(() -> {
            try {
                String jsonResponse = RealtimeToServer.sendRequest(new Request()
                                                     .setAction("GET_HOT_AUCTIONS"));
                GetAllAuctionsResponse response = gson.fromJson(jsonResponse, GetAllAuctionsResponse.class);

                if ("SUCCESS".equals(response.getStatus())) {
                    List<AuctionDTO> hotList = Arrays.asList(response.getData());    
                        Platform.runLater(() -> {
                        AuctionDTO mostBids = (hotList != null && !hotList.isEmpty()) ? hotList.get(0) : null;
                        AuctionDTO highestPrice = (hotList != null && hotList.size() > 1) ? hotList.get(1) : null;
                        AuctionDTO recentlyEnded = (hotList != null && hotList.size() > 2) ? hotList.get(2) : null;
                        
                        displayAuction(mostBids, mostBidsVBox, mostBidsImageView, mostBidsNameLabel, mostBidsTypeLabel, mostBidsPriceLabel, mostBidsStatusLabel);
                        displayAuction(highestPrice, highestPriceVBox, highestPriceImageView, highestPriceNameLabel, highestPriceTypeLabel, highestPricePriceLabel, highestPriceStatusLabel);
                        displayAuction(recentlyEnded, recentlyEndedVBox, recentlyEndedImageView, recentlyEndedNameLabel, recentlyEndedTypeLabel, recentlyEndedPriceLabel, recentlyEndedStatusLabel);
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void displayAuction(AuctionDTO data, VBox vBox, ImageView imageView, Label name, Label type, Label price, Label status) {
        if (data == null || data.getItem() == null) {
            name.setText("CHƯA CÓ DỮ LIỆU");
            type.setText("---");
            price.setText("---");
            status.setText("---");

            imageView.setImage(new Image(getClass().getResourceAsStream("/ddc/client/views/DDCAuction.png")));
            vBox.setOnMouseClicked(null);
            return;
        }

        name.setText(data.getItem().getItemName());
        type.setText(data.getItem().getCategory());
        price.setText(new DecimalFormat("#,###").format(data.getCurrentPrice()) + " đ");
        status.setText(data.getStatus().name());

        // Xử lý hiển thị ảnh sản phẩm
        try {
            String imgUrl = data.getItem().getImageUrl();
            if (imgUrl != null && !imgUrl.isEmpty() && imgUrl.startsWith("http://") || imgUrl.startsWith("https://")) {
                Image img = new Image(imgUrl, true);
                imageView.setImage(img);
            } else {
                imageView.setImage(new Image(getClass().getResourceAsStream("/ddc/client/views/DDCAuction.png")));
            }

            vBox.setCursor(Cursor.HAND);
            vBox.setOnMouseClicked(event -> {
                openAuctionDetail(data, event);
            });
        } catch (Exception e) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/ddc/client/views/DDCAuction.png")));
            LOGGER.error("Khong load dc anh " + data.getItem().getImageUrl());
            e.printStackTrace();
        }
    }

    private void openAuctionDetail(AuctionDTO data, MouseEvent event) {
        String currentBidderId = UserSession.getInstance().getId();
        if (currentBidderId == null || currentBidderId.isBlank()) {
            LOGGER.warn("Không tìm thấy thông tin người dùng (bidderId) trong Session.");
            return;
        }

        try {
            // 1. Tải giao diện trang chi tiết
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ddc/client/views/bidding/auction-detail.fxml"));
            Parent root = loader.load();

            // 2. Lấy controller và truyền dữ liệu vào
            AuctionDetail controller = loader.getController();
            AuctionItemViewModel viewModel = convertToViewModel(data);
            
            controller.setProductData(
                    viewModel.getName(),
                    viewModel.getPrice(),
                    viewModel.getImagePath(),
                    viewModel
            );

            Stage stage = new Stage();
            stage.setTitle(viewModel.getName());
            stage.centerOnScreen();

            Image icon = new Image(getClass().getResourceAsStream("/ddc/client/views/DDCAuction.png"));
            stage.getIcons().add(icon);

            Stage ownerStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.initOwner(ownerStage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            stage.show();

            Platform.runLater(() -> controller.setupAuctionContext(viewModel.getAuctionId(), currentBidderId));

        } catch (IOException e) {
            LOGGER.error("Không thể mở trang chi tiết từ màn hình Home", e);
            e.printStackTrace();
        }
    }

    private AuctionItemViewModel convertToViewModel(AuctionDTO dto) {
        String defaultImage = "/ddc/client/views/DDCAuction.png";
        String fullImageUrl = (dto.getItem().getImageUrl() != null && dto.getItem().getImageUrl().startsWith("http")) 
                            ? dto.getItem().getImageUrl() : defaultImage;
        
        // Tạm thời để thời gian còn lại là chuỗi trống, trang chi tiết sẽ tự kết nối socket để cập nhật lại
        String initialTimeLeft = ""; 

        return new AuctionItemViewModel(
            dto.getAuctionId(),
            dto.getItem().getItemName(),
            new DecimalFormat("#,###").format(dto.getCurrentPrice()) + " đ",
            dto.getEndTime(),
            initialTimeLeft,
            fullImageUrl,
            dto.getItem().getCategory(),
            dto.getStatus()
        );
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
    private void learnMore1 (ActionEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/bidding/Bidding.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void learnMore2 (MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/bidding/Bidding.fxml");
    }
}
