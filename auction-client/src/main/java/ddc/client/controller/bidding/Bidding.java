package ddc.client.controller.bidding;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ddc.client.config.GsonConfig;
import ddc.client.controller.SceneSwitcher;
import ddc.client.controller.notify.NotificationBadgeUtil;
import ddc.client.model.AuctionDTO;
import ddc.client.model.AuctionItemViewModel;
import ddc.client.model.Request;
import ddc.client.network.RequestToServer;
import ddc.client.network.UserSession;
import ddc.client.network.response.GetAllAuctionsResponse;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;

public class Bidding {

    @FXML
    private Label badgeLabel;

    @FXML
    private ScrollPane mainScrollPane;

    @FXML
    private TextField txtSearch;

    @FXML
    private FlowPane auctionContainer;

    @FXML
    private TreeView<String> categoryTree;

    @FXML
    private Label lblResultCount;

    @FXML
    private Label lblSelectedCategory;

    @FXML
    private ImageView imageViewLoading;

    private static final Logger LOGGER = LoggerFactory.getLogger(Bidding.class);

    private final List<AuctionItemViewModel> itemList = new ArrayList<>();
    private final Gson gson = GsonConfig.newGson();
    private final Map<String, CardNodeHolder> cardCache = new HashMap<>();

    private Timeline serverRefreshTimeline;
    private Timeline clockTimeline;

    // bidder hiện tại sẽ được scene trước truyền vào
    private String currentBidderId;

    private String selectedCategory;

    @FXML
    public void initialize() {
        NotificationBadgeUtil.setupBadge(badgeLabel);
        currentBidderId = UserSession.getInstance().getId();
        setupCategoryTree();
        loadingLogo();
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        
        refreshDataFromServer();

        serverRefreshTimeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(5),
            event -> {
                refreshDataFromServer();
            }));
        serverRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        serverRefreshTimeline.play();

        clockTimeline = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1),
            event -> {
                updateAllCountdowns();
            }));
        clockTimeline.setCycleCount(Timeline.INDEFINITE);
        clockTimeline.play();
    }

    /**
     * Gọi hàm này sau khi load Bidding.fxml
     * để truyền bidderId hiện tại từ user/session sang.
     */
    public void setupBidderContext(String bidderId) {
        this.currentBidderId = bidderId;
        applyFilters();
    }

    private void setupCategoryTree() {
        TreeItem<String> root = new TreeItem<>("Root");
        root.setExpanded(true);

        TreeItem<String> art = new TreeItem<>("Nghệ thuật");
        art.getChildren().addAll(
                new TreeItem<>("Hội họa"),
                new TreeItem<>("Điêu khắc"));

        TreeItem<String> elec = new TreeItem<>("Đồ điện tử");
        elec.getChildren().addAll(
                new TreeItem<>("Điện thoại"),
                new TreeItem<>("Máy tính xách tay"),
                new TreeItem<>("Phụ kiện"));

        TreeItem<String> veh = new TreeItem<>("Phương tiện");
        veh.getChildren().addAll(
                new TreeItem<>("Ô tô"),
                new TreeItem<>("Xe máy"));

        root.getChildren().addAll(art, elec, veh);

        categoryTree.setRoot(root);
        categoryTree.setShowRoot(false);

        categoryTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                selectedCategory = null;
            } else {
                selectedCategory = newVal.getValue();
            }
            applyFilters();
        });
    }

    private void toggleLoading(boolean isLoading) {
        if (imageViewLoading != null) {
            imageViewLoading.setVisible(isLoading);
            imageViewLoading.setManaged(isLoading);
        }
    }

    private void refreshDataFromServer() {
        toggleLoading(true);
        String defaultImage = "/ddc/client/views/bidding/image/watch.jpg";

        new Thread(() -> {
            try {
                String JsonResponse = RequestToServer.sendRequest(new Request().setAction("GET_ALL_AUCTIONS"));
                GetAllAuctionsResponse response = gson.fromJson(JsonResponse, GetAllAuctionsResponse.class);
                
                if ("SUCCESS".equals(response.getStatus())) {
                    List<AuctionDTO> auctions = Arrays.asList(response.getData());
                    List<AuctionItemViewModel> newList = new ArrayList<>();
                    
                    for (AuctionDTO auction : auctions) {
                        // Lấy URL từ DB (Bây giờ nó là: https://res.cloudinary.com/...)
                        String imageUrlFromDB = auction.getItem().getImageUrl();
                        LOGGER.debug("Debug URL: {}", imageUrlFromDB); // Kiểm tra xem nó là "abc.jpg" hay
                                                                        // "https://..."

                        // BƯỚC 2: Kiểm tra logic URL
                        String fullImageUrl;
                        if (imageUrlFromDB != null && imageUrlFromDB.startsWith("http")) {
                            // Nếu đã là link (Cloudinary), dùng luôn
                            fullImageUrl = imageUrlFromDB;
                        } else {
                            // Nếu null hoặc không phải link, dùng ảnh mặc định
                            fullImageUrl = defaultImage;
                        }
                        String initialTimeLeft = TimeReturning(auction);

                        newList.add(new AuctionItemViewModel(
                            auction.getAuctionId(),
                            auction.getItem().getItemName(),
                            new DecimalFormat("#,###").format(auction.getCurrentPrice()) + " đ",
                            auction.getEndTime(),
                            initialTimeLeft,
                            fullImageUrl,
                            CategoryTranslating(auction.getItem().getCategory())
                        ));
                    }
                    Platform.runLater(() ->{
                        itemList.clear();
                        itemList.addAll(newList);
                        applyFilters();
                        toggleLoading(false);
                    });
                } else {
                    Platform.runLater(() -> toggleLoading(false));
                    LOGGER.error("Server returned FAILED status.");
                }
                
            } catch (Exception e) {
                Platform.runLater(() -> toggleLoading(false));
                LOGGER.error("Loi load danh sach auction", e);
                e.printStackTrace();
            }
        }).start();
    }

    private void updateAllCountdowns() {
        for (AuctionItemViewModel item : itemList) {
            if (item.getTimeLeft().equals("Đã kết thúc.") || item.getTimeLeft().equals("Sắp bắt đầu.")) {
                continue;
            }
            String newTime = TimeCalculate(LocalDateTime.now(), item.getEndTime());

            if (!newTime.equals(item.getTimeLeft())) {
                item.setTimeLeft(newTime);
            }
        }
    }

    private void applyFilters() {
        String keyword = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase();

        List<AuctionItemViewModel> filtered = itemList.stream()
                .filter(item -> matchesKeyword(item, keyword))
                .filter(item -> matchesCategory(item, selectedCategory))
                .toList();

        renderItems(filtered);
        updateFilterState(filtered.size());
    }

    private boolean matchesKeyword(AuctionItemViewModel item, String keyword) {
        if (keyword.isBlank()) {
            return true;
        }

        return item.getName().toLowerCase().contains(keyword)
                || item.getPrice().toLowerCase().contains(keyword)
                || item.getCategory().toLowerCase().contains(keyword);
    }

    private boolean matchesCategory(AuctionItemViewModel item, String category) {
        if (category == null || category.isBlank()) {
            return true;
        }

        String normalized = category.trim().toLowerCase();
        String itemCategory = item.getCategory() == null ? "" : item.getCategory().trim().toLowerCase();
        String itemName = item.getName() == null ? "" : item.getName().trim().toLowerCase();

        return switch (normalized) {
            case "nghệ thuật", "hội họa", "điêu khắc" -> itemCategory.equals("nghệ thuật");
            case "đồ điện tử", "điện thoại", "máy tính xách tay", "phụ kiện" -> itemCategory.equals("đồ điện tử");
            case "phương tiện", "ô tô", "xe máy" -> itemCategory.equals("phương tiện");
            default -> itemCategory.equals(normalized) || itemName.contains(normalized);
        };
    }

    private void updateFilterState(int resultCount) {
        lblResultCount.setText("Kết quả: " + resultCount);
        lblSelectedCategory.setText(
                selectedCategory == null || selectedCategory.isBlank()
                        ? "Danh mục: Tất cả"
                        : "Danh mục: " + selectedCategory);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private void renderItems(List<AuctionItemViewModel> items) {
        List<Parent> willBeRenderedNodes = new ArrayList<>();

        for (AuctionItemViewModel item : items) {
            try {
                CardNodeHolder holder = cardCache.get(item.getAuctionId());
                
                if (holder == null) {
                    FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/ddc/client/views/bidding/auction-card.fxml"));
                    Parent cardNode = loader.load();
                    AuctionCard cardController = loader.getController();

                    holder = new CardNodeHolder(cardNode, cardController);
                    cardCache.put(item.getAuctionId(), holder);
                }

                holder.controller.setData(item, currentBidderId);
                willBeRenderedNodes.add(holder.cardNode);
            } catch (Exception e) {
                LOGGER.error("Không load được card item", e);
                e.printStackTrace();
            }
        }
        auctionContainer.getChildren().setAll(willBeRenderedNodes);
        cardCache.keySet().removeIf(id -> items.stream().noneMatch(i -> i.getAuctionId().equals(id)));
    }

    private static String TimeReturning(AuctionDTO auction) {
        if (TimeComparing(auction.getStartTime(), LocalDateTime.now())) {
            if (TimeComparing(LocalDateTime.now(), auction.getEndTime())) {
                return TimeCalculate(LocalDateTime.now(), auction.getEndTime());
            } else {
                return "Đã kết thúc.";
            }
        } else {
            return "Sắp bắt đầu.";
        }
    }

    private static String TimeCalculate(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);

        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        String timeRemaining = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return timeRemaining;
    }

    private static boolean TimeComparing(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);

        return !(duration.isNegative() || duration.isZero());
    }

    private static String CategoryTranslating(String category) {
        switch (category) {
            case "GENERAL" -> {
                return "Chung";
            }
            case "ART" -> {
                return "Nghệ thuật";
            }
            case "VEHICLE" -> {
                return "Phương tiện";
            }
            case "ELECTRONICS" -> {
                return "Đồ điện tử";
            }
            default -> { return "Khác"; }
        }
    }

    private void loadingLogo () {
        RotateTransition rotate = new RotateTransition();
        rotate.setNode(imageViewLoading);
        rotate.setByAngle(360);
        rotate.setDuration(javafx.util.Duration.seconds(1));
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);

        rotate.play();
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleClearFilters() {
        txtSearch.clear();
        categoryTree.getSelectionModel().clearSelection();
        selectedCategory = null;
        applyFilters();
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleScrollTop() {
        mainScrollPane.setVvalue(0);
    }

    private void stopAutoRefresh() {
        if (serverRefreshTimeline != null) {
            serverRefreshTimeline.stop();
        }
    }

    private static class CardNodeHolder {
        final Parent cardNode;
        final AuctionCard controller;

        CardNodeHolder(Parent cardNode, AuctionCard controller) {
            this.cardNode = cardNode;
            this.controller = controller;
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToHome(MouseEvent event) {
        stopAutoRefresh();
        SceneSwitcher.goTo(event, "/ddc/client/views/home/Home.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToSelling(MouseEvent event) {
        stopAutoRefresh();
        SceneSwitcher.goTo(event, "/ddc/client/views/selling/Selling.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToProfile(MouseEvent event) {
        stopAutoRefresh();
        SceneSwitcher.goTo(event, "/ddc/client/views/profile/Profile.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToNotify(MouseEvent event) {
        stopAutoRefresh();
        SceneSwitcher.goTo(event, "/ddc/client/views/notify/Notify.fxml");
    }
}
