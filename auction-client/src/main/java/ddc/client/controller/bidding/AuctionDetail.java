package ddc.client.controller.bidding;

import ddc.client.config.ClientContext;
import ddc.client.controller.SceneSwitcher;
import ddc.client.network.client.AuctionSocketClient;
import ddc.client.network.listener.ServerMessageListener;
import ddc.client.network.response.AuctionEventResponse;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class AuctionDetail implements ServerMessageListener {

    @FXML
    private Label lblStartTime;

    @FXML
    private Label lblEndTime;

    @FXML
    private ImageView mainImage;

    @FXML
    private Label lblProductName;

    @FXML
    private Label lblPrice;

    @FXML
    private Label lblCountdown;

    @FXML
    private Label lblMessage;

    @FXML
    private TextField txtBidAmount;

    @FXML
    private FlowPane thumbnailContainer;

    private AuctionSocketClient socketClient;
    private String currentAuctionId;
    private String currentBidderId;

    @FXML
    public void initialize() {
        socketClient = new AuctionSocketClient("localhost", 5555);
        socketClient.setListener(this);

        if (lblMessage != null) {
            lblMessage.setText("Chưa có cập nhật.");
        }

        if (lblStartTime != null) {
            lblStartTime.setText("--:--");
        }

        if (lblEndTime != null) {
            lblEndTime.setText("--:--");
        }

        if (lblCountdown != null) {
            lblCountdown.setText("Đang chờ dữ liệu...");
        }
    }

    public void setProductData(String name, String price, String imagePath) {
        if (lblProductName != null) {
            lblProductName.setText(name);
        }

        if (lblPrice != null) {
            lblPrice.setText(price);
        }

        try {
            var imageUrl = getClass().getResource(imagePath);
            if (imageUrl != null) {
                mainImage.setImage(new Image(imageUrl.toExternalForm(), true));
            }
        } catch (Exception e) {
            System.out.println("Lỗi load ảnh chi tiết");
        }
    }

    public void setupAuctionContext(String auctionId, String bidderId) {
        this.currentAuctionId = auctionId;
        this.currentBidderId = bidderId;

        setMessage("Đang kết nối tới phiên đấu giá...");

        ClientContext.EXECUTOR.execute(() -> {
            try {
                if (!socketClient.isConnected()) {
                    socketClient.connect();
                }
                socketClient.subscribeAuction(auctionId);
                Platform.runLater(() -> setMessage("Đã kết nối tới phiên đấu giá."));
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> setMessage("Không kết nối được server: " + e.getMessage()));
            }
        });
    }

    @FXML
    private void handlePlaceBid() {
        if (currentAuctionId == null || currentBidderId == null) {
            setMessage("Thiếu auctionId hoặc bidderId.");
            return;
        }

        if (txtBidAmount == null || txtBidAmount.getText() == null || txtBidAmount.getText().isBlank()) {
            setMessage("Vui lòng nhập số tiền ra giá.");
            return;
        }

        final double amount;
        try {
            amount = Double.parseDouble(txtBidAmount.getText().trim());
        } catch (NumberFormatException e) {
            setMessage("Số tiền không hợp lệ.");
            return;
        }

        if (amount <= 0) {
            setMessage("Số tiền phải lớn hơn 0.");
            return;
        }

        setMessage("Đang gửi yêu cầu ra giá...");

        ClientContext.EXECUTOR.execute(() -> {
            try {
                socketClient.placeBid(currentAuctionId, currentBidderId, amount);
                Platform.runLater(() -> {
                    txtBidAmount.clear();
                    setMessage("Đã gửi yêu cầu ra giá.");
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> setMessage("Không gửi được bid: " + e.getMessage()));
            }
        });
    }

    @Override
    public void onAuctionEvent(AuctionEventResponse event) {
        if (event == null) {
            return;
        }

        if (currentAuctionId != null
                && event.getAuctionId() != null
                && !currentAuctionId.equals(event.getAuctionId())) {
            return;
        }

        Platform.runLater(() -> {
            if (lblPrice != null) {
                lblPrice.setText(formatPrice(event.getCurrentPrice()));
            }

            String eventType = event.getEventType();
            if ("SNAPSHOT".equals(eventType)) {
                setMessage("Đã tải dữ liệu phiên đấu giá.");
            } else if ("NEW_BID".equals(eventType)) {
                setMessage("Bid mới từ " + event.getBidderName() + ": " + formatPrice(event.getBidAmount()));
            } else {
                setMessage(event.getMessage() != null ? event.getMessage() : "Có cập nhật mới.");
            }
        });
    }

    @Override
    public void onError(String message) {
        Platform.runLater(() -> setMessage(message));
    }

    @Override
    public void onDisconnected(String message) {
        Platform.runLater(() -> setMessage(message));
    }

    private void setMessage(String message) {
        if (lblMessage != null) {
            lblMessage.setText(message);
        } else {
            System.out.println(message);
        }
    }

    private String formatPrice(double value) {
        return String.format("%,.0f đ", value);
    }

    @FXML
    private void handleBackToBidding(MouseEvent event) {
        Node source = (Node) event.getSource();

        Stage stage = (Stage) source.getScene().getWindow();

        stage.close();
    }

    @FXML
    private void switchToHome(MouseEvent event) {
        cleanup();
        SceneSwitcher.goTo(event, "/ddc/client/views/home/Home.fxml");
    }

    @FXML
    private void switchToSelling(MouseEvent event) {
        cleanup();
        SceneSwitcher.goTo(event, "/ddc/client/views/selling/Selling.fxml");
    }

    @FXML
    private void switchToProfile(MouseEvent event) {
        cleanup();
        SceneSwitcher.goTo(event, "/ddc/client/views/profile/Profile.fxml");
    }

    @FXML
    private void switchToNotify(MouseEvent event) {
        cleanup();
        SceneSwitcher.goTo(event, "/ddc/client/views/notify/Notify.fxml");
    }

    public void cleanup() {
        if (socketClient != null) {
            socketClient.disconnect();
        }
    }
}