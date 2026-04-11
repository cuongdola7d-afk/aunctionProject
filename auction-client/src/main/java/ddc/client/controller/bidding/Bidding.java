package ddc.client.controller.bidding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ddc.client.controller.SceneSwitcher;
import ddc.client.model.AuctionItemViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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

    private final List<AuctionItemViewModel> itemList = new ArrayList<>();

    // bidder hiện tại sẽ được scene trước truyền vào
    private String currentBidderId;

    @FXML
    public void initialize() {
        loadSampleData();
        renderItems(itemList);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterItems(newValue);
        });

        setupCategoryTree();
    }

    /**
     * Gọi hàm này sau khi load Bidding.fxml
     * để truyền bidderId hiện tại từ user/session sang.
     */
    public void setupBidderContext(String bidderId) {
        this.currentBidderId = bidderId;
        renderItems(itemList);
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
        TreeItem<String> accessories = new TreeItem<>("Phụ kiện");
        TreeItem<String> laptops = new TreeItem<>("Máy tính xách tay");
        TreeItem<String> smartphones = new TreeItem<>("Điện thoại");

        elec.getChildren().addAll(smartphones, laptops, accessories);

        TreeItem<String> veh = new TreeItem<>("Phương tiện");
        veh.getChildren().addAll(
                new TreeItem<>("Ô tô"),
                new TreeItem<>("Xe máy")
        );

        root.getChildren().addAll(art, elec, veh);

        categoryTree.setRoot(root);
        categoryTree.setShowRoot(false);

        categoryTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.isLeaf()) {
                filterByCategory(newVal.getValue());
            }
        });
    }

    private void filterByCategory(String category) {
        String lowerCategory = category.toLowerCase();

        List<AuctionItemViewModel> filtered = itemList.stream()
                .filter(item -> item.getName().toLowerCase().contains(lowerCategory)
                        || (lowerCategory.equals("laptops")
                        && item.getName().toLowerCase().contains("macbook")))
                .toList();

        renderItems(filtered);
    }

    private void loadSampleData() {
        String demoAuctionId = "06f57c1e-0319-4131-85d9-e855608100cd";

        itemList.add(new AuctionItemViewModel(demoAuctionId, "Đồng hồ thông minh", "1,250,000 đ", "02:15:30",
                "/ddc/client/views/bidding/image/watch.jpg", "Đồ điện tử"));
        itemList.add(new AuctionItemViewModel(demoAuctionId, "Đồng hồ Vintage", "3,400,000 đ", "00:45:12",
                "/ddc/client/views/bidding/image/vintageWatch.jpg", "Đồ điện tử"));
        itemList.add(new AuctionItemViewModel(demoAuctionId, "Tai nghe chống ồn", "850,000 đ", "05:10:00",
                "/ddc/client/views/bidding/image/headphone.jpg", "Đồ điện tử"));
        itemList.add(new AuctionItemViewModel(demoAuctionId, "Bàn phím cơ RGB", "2,100,000 đ", "01:20:45",
                "/ddc/client/views/bidding/image/mechanicalKeyboard.jpg", "Đồ điện tử"));
    }

    private void renderItems(List<AuctionItemViewModel> items) {
        auctionContainer.getChildren().clear();

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

    private void filterItems(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            renderItems(itemList);
            return;
        }

        String lowerKeyword = keyword.toLowerCase().trim();

        List<AuctionItemViewModel> filtered = itemList.stream()
                .filter(item -> item.getName().toLowerCase().contains(lowerKeyword))
                .toList();

        renderItems(filtered);
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