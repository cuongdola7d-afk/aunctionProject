package ddc.client.controller.bidding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ddc.client.controller.SceneSwitcher;
import ddc.client.model.AuctionItemViewModel;
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

    // bidder hiện tại sẽ được scene trước truyền vào
    //private String currentBidderId;
    private String currentBidderId = "BIDDER-001";

    private String selectedCategory;

    @FXML
    public void initialize() {
        currentBidderId = "BIDDER-001";

        loadSampleData();
        setupCategoryTree();

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        applyFilters();
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

    private void loadSampleData() {
        itemList.clear();

        

        itemList.add(new AuctionItemViewModel(
                "AUCT-001",
                "Đồng hồ thông minh",
                "1,250,000 đ",
                "02:15:30",
                "/ddc/client/views/bidding/image/watch.jpg",
                "Đồ điện tử"
        ));

        itemList.add(new AuctionItemViewModel(
                "AUCT-002",
                "Đồng hồ Vintage",
                "3,400,000 đ",
                "00:45:12",
                "/ddc/client/views/bidding/image/vintageWatch.jpg",
                "Nghệ thuật"
        ));

        itemList.add(new AuctionItemViewModel(
                "AUCT-003",
                "Tai nghe chống ồn",
                "850,000 đ",
                "05:10:00",
                "/ddc/client/views/bidding/image/headphone.jpg",
                "Đồ điện tử"
        ));

        itemList.add(new AuctionItemViewModel(
                "AUCT-004",
                "Bàn phím cơ RGB",
                "2,100,000 đ",
                "01:20:45",
                "/ddc/client/views/bidding/image/mechanicalKeyboard.jpg",
                "Đồ điện tử"
        ));
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

    @FXML
    private void handleClearFilters() {
        txtSearch.clear();
        categoryTree.getSelectionModel().clearSelection();
        selectedCategory = null;
        applyFilters();
    }

    @FXML
    private void handleScrollTop() {
        mainScrollPane.setVvalue(0);
    }

    @FXML
    private void switchToHome(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/home/Home.fxml");
    }

    @FXML
    private void switchToSelling(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/selling/Selling.fxml");
    }

    @FXML
    private void switchToProfile(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/profile/Profile.fxml");
    }

    @FXML
    private void switchToNotify(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/notify/Notify.fxml");
    }
}