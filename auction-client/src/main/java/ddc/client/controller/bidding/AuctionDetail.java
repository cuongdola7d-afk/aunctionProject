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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuctionDetail implements ServerMessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuctionDetail.class);

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
    private long minBidIncrement; // bước giá tối thiểu từ server

    @FXML
    public void initialize() {
        socketClient = new AuctionSocketClient(ClientContext.SERVER_HOST, ClientContext.REALTIME_PORT);

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
            var imageUrl = imagePath.startsWith("http://") || imagePath.startsWith("https://")
                    ? null
                    : getClass().getResource(imagePath);
            if (imageUrl != null) {
                mainImage.setImage(new Image(imageUrl.toExternalForm(), true));
            } else if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
                mainImage.setImage(new Image(imagePath, true));
            }
        } catch (Exception e) {
            LOGGER.warn("Lỗi load ảnh chi tiết");
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
                LOGGER.error("Loi ket noi auction", e);
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

        final long amount;
        try {
            double raw = Double.parseDouble(txtBidAmount.getText().trim());
            // Kiểm tra giá phải là số nguyên
            if (raw != Math.floor(raw)) {
                setMessage("Giá đặt phải là số nguyên!");
                return;
            }
            amount = (long) raw;
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
                LOGGER.error("Loi gui bid", e);
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
            // Cập nhật bước giá tối thiểu từ server
            if (event.getMinBidIncrement() > 0) {
                minBidIncrement = event.getMinBidIncrement();
            }
            // Cập nhật endTime nếu server gửi (anti-snip gia hạn)
            if (event.getEndTime() != null && lblEndTime != null) {
                lblEndTime.setText(event.getEndTime());
            }

            String eventType = event.getEventType();
            if ("SNAPSHOT".equals(eventType)) {
                setMessage("Đã tải dữ liệu phiên đấu giá.");
            } else if ("NEW_BID".equals(eventType)) {
                String msg = "Bid mới từ " + event.getBidderName() + ": " + formatPrice(event.getBidAmount());
                if (event.isTimeExtended()) {
                    msg += "Thời gian được gia hạn";
                }
                setMessage(msg);
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
            LOGGER.info(message);
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
