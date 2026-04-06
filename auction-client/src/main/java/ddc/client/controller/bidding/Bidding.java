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
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.TreeItem;

public class Bidding {

    @FXML
    private ScrollPane mainScrollPane;

    @FXML
    private TextField txtSearch;

    @FXML
    private FlowPane auctionContainer;

    private final List<AuctionItemViewModel> itemList = new ArrayList<>();

    @FXML
    public void initialize() {
        loadSampleData();
        renderItems(itemList);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filterItems(newValue);
        });

        setupCategoryTree();//logic khởi tạo TreeView
    }

private void setupCategoryTree() {
    // Tạo nút Gốc (vẫn để Root và ẩn đi)
    TreeItem<String> root = new TreeItem<>("Root");
    root.setExpanded(true);

    // 1. Danh mục Nghệ thuật
    TreeItem<String> art = new TreeItem<>("Nghệ thuật");
    art.getChildren().addAll(
        new TreeItem<>("Hội họa"),
        new TreeItem<>("Điêu khắc")
    );

    // 2. Danh mục Đồ điện tử
    TreeItem<String> elec = new TreeItem<>("Đồ điện tử");
    TreeItem<String> accessories = new TreeItem<>("Phụ kiện");
    TreeItem<String> laptops = new TreeItem<>("Máy tính xách tay");
    TreeItem<String> smartphones = new TreeItem<>("Điện thoại");
    
    elec.getChildren().addAll(smartphones, laptops, accessories);

    // 3. Danh mục Phương tiện
    TreeItem<String> veh = new TreeItem<>("Phương tiện");
    veh.getChildren().addAll(
        new TreeItem<>("Ô tô"),
        new TreeItem<>("Xe máy")
    );

    // Thêm tất cả vào gốc
    root.getChildren().addAll(art, elec, veh);
    
    categoryTree.setRoot(root);
    categoryTree.setShowRoot(false);

    // Cập nhật lại sự kiện lắng nghe để khớp với tiếng Việt
    categoryTree.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
        if (newVal != null && newVal.isLeaf()) {
            filterByCategory(newVal.getValue());
        }
    });
}

    // Hàm bổ trợ để lọc sản phẩm theo danh mục (tương tự filterItems của bạn)
    private void filterByCategory(String category) {
        // Giả sử chúng ta lọc dựa trên tên hoặc một thuộc tính category (nếu model của bạn có)
        // Ở đây mình ví dụ lọc theo từ khóa trong tên sản phẩm cho đơn giản
        String lowerCategory = category.toLowerCase();
        
        List<AuctionItemViewModel> filtered = itemList.stream()
                .filter(item -> item.getName().toLowerCase().contains(lowerCategory) || 
                                // Nếu là "Laptops" thì tìm các mục có tên "MacBook" chẳng hạn
                                (lowerCategory.equals("laptops") && item.getName().toLowerCase().contains("macbook")))
                .toList();

        renderItems(filtered);
    }

    private void loadSampleData() {
        itemList.add(new AuctionItemViewModel("Đồng hồ thông minh", "1,250,000 đ", "02:15:30", "/ddc/client/views/bidding/image/watch.jpg", "Đồ điện tử"));
        itemList.add(new AuctionItemViewModel("Đồng hồ Vintage", "3,400,000 đ", "00:45:12", "/ddc/client/views/bidding/image/vintageWatch.jpg", "Đồ điện tử"));
        itemList.add(new AuctionItemViewModel("Tai nghe chống ồn", "850,000 đ", "05:10:00", "/ddc/client/views/bidding/image/headphone.jpg", "Đồ điện tử"));
        itemList.add(new AuctionItemViewModel("Bàn phím cơ RGB", "2,100,000 đ", "01:20:45", "/ddc/client/views/bidding/image/mechanicalKeyboard.jpg", "Đồ điện tử"));
        itemList.add(new AuctionItemViewModel("Màn hình 4K", "6,500,000 đ", "12:05:00", "/ddc/client/views/bidding/image/monitor.jpg", "Đồ điện tử"));
        itemList.add(new AuctionItemViewModel("Chuột Gaming Wireless", "1,150,000 đ", "00:15:00", "/ddc/client/views/bidding/image/mouse.jpg", "Đồ điện tử"));
        itemList.add(new AuctionItemViewModel("MacBook Pro", "22,000,000 đ", "23:45:10", "/ddc/client/views/bidding/image/laptop.jpg", "Đồ điện tử"));
        itemList.add(new AuctionItemViewModel("Loa Bluetooth", "4,200,000 đ", "08:30:00", "/ddc/client/views/bidding/image/speaker.jpg", "Đồ điện tử"));

        itemList.add(new AuctionItemViewModel("Đồng hồ thể thao", "1,850,000 đ", "03:20:10", "/ddc/client/views/bidding/image/watch.jpg", "Đồ điện tử"));
        itemList.add(new AuctionItemViewModel("Máy ảnh Canon", "5,400,000 đ", "00:25:00", "/ddc/client/views/bidding/image/camera.jpg", "Đồ điện tử"));
        itemList.add(new AuctionItemViewModel("Tai nghe Gaming", "1,100,000 đ", "06:40:32", "/ddc/client/views/bidding/image/headphone.jpg", "Đồ điện tử"));
        itemList.add(new AuctionItemViewModel("Bàn phím Bluetooth", "950,000 đ", "01:55:12", "/ddc/client/views/bidding/image/bluetoothKeyboard.jpg", "Đồ điện tử"));
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
                cardController.setData(item);

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
    private void switchToSelling (MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/selling/Selling.fxml");
    }

    @FXML
    private void switchToProfile (MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/profile/Profile.fxml");
    }

    @FXML
    private TreeView<String> categoryTree;


    
}