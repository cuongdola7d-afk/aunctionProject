package ddc.client.controller.bidding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddc.client.config.ClientContext;
import ddc.client.controller.SceneSwitcher;
import ddc.client.model.AuctionItemViewModel;
import ddc.client.network.client.AuctionSocketClient;
import ddc.client.network.listener.ServerMessageListener;
import ddc.client.network.response.AuctionEventResponse;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

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

    @FXML private LineChart<String, Number> priceChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    private XYChart.Series<String, Number> priceSeries;

    private AuctionSocketClient socketClient;
    private String currentAuctionId;
    private String currentBidderId;
    private AuctionItemViewModel currentItem;
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

        priceSeries = new XYChart.Series<>();
        priceSeries.setName("Giá hiện tại");
        priceChart.getData().add(priceSeries);
        // Tối ưu cho Realtime
        xAxis.setAnimated(false);
        priceChart.setAnimated(false);
    }

    public void setProductData(String name, String price, String imagePath, AuctionItemViewModel item) {
        this.currentItem = item;
        if (lblProductName != null) {
            lblProductName.setText(name);
        }

        if (lblPrice != null) {
            lblPrice.setText(price);
        }

        if (priceSeries != null && item != null) {
            priceSeries.getData().clear();
            // QUAN TRỌNG: Load lại toàn bộ lịch sử đã lưu trong ViewModel
            for (XYChart.Data<String, Number> data : item.getPriceHistory()) {
                // Phải tạo đối tượng Data mới để tránh lỗi "Duplicate Child" trong JavaFX
                priceSeries.getData().add(new XYChart.Data<>(data.getXValue(), data.getYValue()));
            }
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

    private void addPricePoint(double price) {
        String time = java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM\nHH:mm:ss")
        );
        
        // 1. Thêm vào biểu đồ hiện tại
        XYChart.Data<String, Number> newData = new XYChart.Data<>(time, price);
        priceSeries.getData().add(newData);

        // 2. Lưu vào lịch sử trong ViewModel để khi mở lại vẫn thấy định dạng này
        if (currentItem != null) {
            currentItem.getPriceHistory().add(new XYChart.Data<>(time, price));
            
            if (currentItem.getPriceHistory().size() > 15) {
                currentItem.getPriceHistory().remove(0);
            }
        }

        // 3. Giới hạn số điểm hiển thị
        if (priceSeries.getData().size() > 15) {
            priceSeries.getData().remove(0);
        }
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

            if ("NEW_BID".equals(event.getEventType()) || "SNAPSHOT".equals(event.getEventType())) {
                addPricePoint(event.getCurrentPrice());
            }

            String eventType = event.getEventType();
            if ("CANCELLED".equals(eventType) || "CANCELLED".equals(event.getStatus())) {
                if (lblCountdown != null) {
                    lblCountdown.setText("Da huy");
                }
                setMessage(event.getMessage() != null ? event.getMessage() : "Phien dau gia da bi huy.");
            } else if ("FINISHED".equals(eventType) || "FINISHED".equals(event.getStatus())) {
                setMessage(event.getMessage() != null ? event.getMessage() : "Phien dau gia da ket thuc.");
            } else if ("SNAPSHOT".equals(eventType)) {
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
        // 1. Lấy Stage và đóng ngay lập tức để người dùng không cảm thấy bị treo
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();

        // 2. Chạy cleanup ở luồng phụ (EXECUTOR) để tránh block UI Thread
        ddc.client.config.ClientContext.EXECUTOR.execute(() -> {
            try {
                cleanup();
            } catch (Exception e) {
                LOGGER.error("Lỗi khi cleanup: " + e.getMessage());
            }
        });
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
