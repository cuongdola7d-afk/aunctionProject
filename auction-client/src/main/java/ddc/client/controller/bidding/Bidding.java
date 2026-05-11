package ddc.client.controller.bidding;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

import ddc.client.config.GsonConfig;
import ddc.client.controller.SceneSwitcher;
import ddc.client.model.AuctionDTO;
import ddc.client.model.AuctionItemViewModel;
import ddc.client.model.Request;
import ddc.client.network.RealtimeToServer;
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

    private final List<AuctionItemViewModel> itemList = new ArrayList<>();
    private final Gson gson = GsonConfig.newGson();

    private Timeline serverRefreshTimeline;
    private Timeline clockTimeline;

    // bidder hiện tại sẽ được scene trước truyền vào
    //private String currentBidderId;
    private String currentBidderId = "BIDDER-001";

    private String selectedCategory;

    @FXML
    public void initialize() {
        currentBidderId = "BIDDER-001";
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
                new TreeItem<>("Điêu khắc")
        );

        TreeItem<String> elec = new TreeItem<>("Đồ điện tử");
        elec.getChildren().addAll(
                new TreeItem<>("Điện thoại"),
                new TreeItem<>("Máy tính xách tay"),
                new TreeItem<>("Phụ kiện")
        );

        TreeItem<String> veh = new TreeItem<>("Phương tiện");
        veh.getChildren().addAll(
                new TreeItem<>("Ô tô"),
                new TreeItem<>("Xe máy")
        );

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
                        : "Danh mục: " + selectedCategory
        );
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
                        getClass().getResource("/ddc/client/views/bidding/auction-card.fxml")
                );

                Parent card = loader.load();

                AuctionCard cardController = loader.getController();
                cardController.setData(item, currentBidderId);

                auctionContainer.getChildren().add(card);

            } catch (IOException e) {
                System.out.println("Không load được card item");
                e.printStackTrace();
            }
        }
    }

    private static String TimeCalculate (LocalDateTime start, LocalDateTime end) {
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

    private static String CategoryTranslating (String category) {
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