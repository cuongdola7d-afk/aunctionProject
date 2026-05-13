package ddc.client.controller.bidding;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ddc.client.config.GsonConfig;
import ddc.client.controller.SceneSwitcher;
import ddc.client.model.AuctionDTO;
import ddc.client.model.AuctionItemViewModel;
import ddc.client.model.Request;
import ddc.client.network.RealtimeToServer;
import ddc.client.network.UserSession;
import ddc.client.network.response.GetAllAuctionsResponse;
import javafx.animation.KeyFrame;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;

public class Bidding {

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
    private Label lblEmptyState;

    private static final Logger LOGGER = LoggerFactory.getLogger(Bidding.class);

    private final List<AuctionItemViewModel> itemList = new ArrayList<>();
    private final Gson gson = GsonConfig.newGson();

    private Timeline serverRefreshTimeline;
    private Timeline clockTimeline;

    // bidder hiện tại sẽ được scene trước truyền vào
    private String currentBidderId;

    private String selectedCategory;

    @FXML
    public void initialize() {
        currentBidderId = UserSession.getInstance().getId();
        // loadSampleData();
        setupCategoryTree();
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

    private void refreshDataFromServer() {
        new Thread(() -> {
            try {
                String JsonResponse = RealtimeToServer.sendRequest(new Request().setAction("GET_ALL"));
                GetAllAuctionsResponse response = gson.fromJson(JsonResponse, GetAllAuctionsResponse.class);
                
                if ("SUCCESS".equals(response.getStatus())) {
                    List<AuctionDTO> auctions = Arrays.asList(response.getData());
                    List<AuctionItemViewModel> newList = new ArrayList<>();
                    
                    for (AuctionDTO auction : auctions) {
                        String initialTimeLeft = TimeCalculate(LocalDateTime.now(), auction.getEndTime());

                        newList.add(new AuctionItemViewModel(
                            auction.getAuctionId(),
                            auction.getItem().getItemName(),
                            new DecimalFormat("#,###").format(auction.getCurrentPrice()) + " đ",
                            auction.getEndTime(),
                            initialTimeLeft,
                            "/ddc/client/views/bidding/image/watch.jpg",
                            CategoryTranslating(auction.getItem().getCategory())
                        ));
                    }
                    Platform.runLater(() ->{
                        itemList.clear();
                        itemList.addAll(newList);
                        applyFilters();
                    });
                }
                
            } catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    private void updateAllCountdowns() {
        for (AuctionItemViewModel item : itemList) {
            String newTime = TimeCalculate(LocalDateTime.now(), item.getEndTime());

            if (!newTime.equals(item.getTimeLeft())) {
                item.setTimeLeft(newTime);
            }
        }
    }
    // private void loadSampleData() {
    //     itemList.clear();

    //     // BƯỚC 1: Xóa bỏ việc ghép imageBaseUrl với IP Server vì link giờ nằm trên
    //     // Cloud
    //     // Bạn có thể giữ lại ảnh mặc định để dự phòng
    //     String defaultImage = "/ddc/client/views/bidding/image/watch.jpg";

    //     new Thread(() -> {
    //         try {
    //             String JsonResponse = RealtimeToServer.sendRequest(new Request().setAction("GET_ALL"));
    //             GetAllAuctionsResponse response = gson.fromJson(JsonResponse, GetAllAuctionsResponse.class);

    //             if (response != null && "SUCCESS".equals(response.getStatus())) {
    //                 List<AuctionDTO> auctions = Arrays.asList(response.getData());

    //                 Platform.runLater(() -> {
    //                     for (AuctionDTO auction : auctions) {
    //                         // Lấy URL từ DB (Bây giờ nó là: https://res.cloudinary.com/...)
    //                         String imageUrlFromDB = auction.getItem().getImageUrl();
    //                         LOGGER.debug("Debug URL: {}", imageUrlFromDB); // Kiểm tra xem nó là "abc.jpg" hay
    //                                                                        // "https://..."

    //                         // BƯỚC 2: Kiểm tra logic URL
    //                         String fullImageUrl;
    //                         if (imageUrlFromDB != null && imageUrlFromDB.startsWith("http")) {
    //                             // Nếu đã là link (Cloudinary), dùng luôn
    //                             fullImageUrl = imageUrlFromDB;
    //                         } else {
    //                             // Nếu null hoặc không phải link, dùng ảnh mặc định
    //                             fullImageUrl = defaultImage;
    //                         }

    //                         itemList.add(new AuctionItemViewModel(
    //                                 auction.getAuctionId(),
    //                                 auction.getItem().getItemName(),
    //                                 new DecimalFormat("#,###").format(auction.getCurrentPrice()) + " đ",
    //                                 TimeCalculate(LocalDateTime.now(), auction.getEndTime()),
    //                                 fullImageUrl, // Truyền link trực tiếp vào ViewModel
    //                                 CategoryTranslating(auction.getItem().getCategory())));
    //                     }

    //                     itemList.add(new AuctionItemViewModel(
    //                             "AUCT-001",
    //                             "Đồng hồ thông minh",
    //                             "1,250,000 đ",
    //                             "02:15:30",
    //                             "/ddc/client/views/bidding/image/watch.jpg",
    //                             "Đồ điện tử"));

    //                     itemList.add(new AuctionItemViewModel(
    //                             "AUCT-002",
    //                             "Đồng hồ Vintage",
    //                             "3,400,000 đ",
    //                             "00:45:12",
    //                             "/ddc/client/views/bidding/image/vintageWatch.jpg",
    //                             "Nghệ thuật"));

    //                     itemList.add(new AuctionItemViewModel(
    //                             "AUCT-003",
    //                             "Tai nghe chống ồn",
    //                             "850,000 đ",
    //                             "05:10:00",
    //                             "/ddc/client/views/bidding/image/headphone.jpg",
    //                             "Đồ điện tử"));

    //                     itemList.add(new AuctionItemViewModel(
    //                             "AUCT-004",
    //                             "Bàn phím cơ RGB",
    //                             "2,100,000 đ",
    //                             "01:20:45",
    //                             "/ddc/client/views/bidding/image/mechanicalKeyboard.jpg",
    //                             "Đồ điện tử"));
    //                 });
    //             }
    //         } catch (Exception e) {
    //             LOGGER.error("Loi load auction data", e);
    //         }
    //     }).start();
    // }

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
        auctionContainer.getChildren().clear();

        boolean isEmpty = items.isEmpty();
        lblEmptyState.setVisible(isEmpty);
        lblEmptyState.setManaged(isEmpty);

        for (AuctionItemViewModel item : items) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/ddc/client/views/bidding/auction-card.fxml"));

                Parent card = loader.load();

                AuctionCard cardController = loader.getController();
                cardController.setData(item, currentBidderId);

                auctionContainer.getChildren().add(card);

            } catch (IOException e) {
                LOGGER.error("Không load được card item", e);

            }
        }
    }

    private static String TimeCalculate(LocalDateTime start, LocalDateTime end) {
        Duration duration = Duration.between(start, end);

        if (duration.isNegative() || duration.isZero()) {
            return "Đã kết thúc.";
        }

        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        String timeRemaining = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return timeRemaining;
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
            default -> throw new AssertionError();
        }
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
