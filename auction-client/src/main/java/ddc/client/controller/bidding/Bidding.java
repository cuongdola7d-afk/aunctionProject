package ddc.client.controller.bidding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ddc.client.model.AuctionItemViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

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
    }

    private void loadSampleData() {
        itemList.add(new AuctionItemViewModel("Đồng hồ thông minh", "1,250,000 đ", "02:15:30", "/ddc/client/views/bidding/image/watch.jpg"));
        itemList.add(new AuctionItemViewModel("Máy ảnh Vintage", "3,400,000 đ", "00:45:12", "/ddc/client/views/bidding/image/camera.jpg"));
        itemList.add(new AuctionItemViewModel("Tai nghe chống ồn", "850,000 đ", "05:10:00", "/ddc/client/views/bidding/image/headphone.jpg"));
        itemList.add(new AuctionItemViewModel("Bàn phím cơ RGB", "2,100,000 đ", "01:20:45", "/ddc/client/views/bidding/image/keyboard.jpg"));
        itemList.add(new AuctionItemViewModel("Màn hình 4K", "6,500,000 đ", "12:05:00", "/ddc/client/views/bidding/image/monitor.jpg"));
        itemList.add(new AuctionItemViewModel("Chuột Gaming Wireless", "1,150,000 đ", "00:15:00", "/ddc/client/views/bidding/image/mouse.jpg"));
        itemList.add(new AuctionItemViewModel("MacBook Pro", "22,000,000 đ", "23:45:10", "/ddc/client/views/bidding/image/laptop.jpg"));
        itemList.add(new AuctionItemViewModel("Loa Bluetooth", "4,200,000 đ", "08:30:00", "/ddc/client/views/bidding/image/speaker.jpg"));

        itemList.add(new AuctionItemViewModel("Đồng hồ thể thao", "1,850,000 đ", "03:20:10", "/ddc/client/views/bidding/image/watch.jpg"));
        itemList.add(new AuctionItemViewModel("Máy ảnh Canon", "5,400,000 đ", "00:25:00", "/ddc/client/views/bidding/image/camera.jpg"));
        itemList.add(new AuctionItemViewModel("Tai nghe Gaming", "1,100,000 đ", "06:40:32", "/ddc/client/views/bidding/image/headphone.jpg"));
        itemList.add(new AuctionItemViewModel("Bàn phím Bluetooth", "950,000 đ", "01:55:12", "/ddc/client/views/bidding/image/keyboard.jpg"));
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
}