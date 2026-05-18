package ddc.client.model;

import java.time.LocalDateTime;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

public class AuctionItemViewModel {
    private final String auctionId;
    private String name;
    private String price;
    private StringProperty timeLeft = new SimpleStringProperty();
    private LocalDateTime endTime;
    private String imagePath;
    private String category;
    private static final Map<String, ObservableList<XYChart.Data<String, Number>>> GLOBAL_PRICE_HISTORY = new java.util.concurrent.ConcurrentHashMap<>();
    private final ObservableList<XYChart.Data<String, Number>> priceHistory;

    
    public AuctionItemViewModel(String auctionId, String name, String price, LocalDateTime endTime, String timeLeft, String imagePath, String category) {
        this.auctionId = auctionId;
        this.name = name;
        this.price = price;
        this.endTime = endTime;
        this.timeLeft.set(timeLeft);
        this.imagePath = imagePath;
        this.category = category;
        this.priceHistory = GLOBAL_PRICE_HISTORY.computeIfAbsent(auctionId, k -> FXCollections.observableArrayList());
    }

    public ObservableList<XYChart.Data<String, Number>> getPriceHistory() {
        return priceHistory;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public StringProperty timeLeftProperty() {
        return timeLeft;
    }

    public String getTimeLeft() {
        return timeLeft.get();
    }

    public void setTimeLeft(String timeLeft) {
        this.timeLeft.set(timeLeft);
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getCategory() {
        return category;
    }


}
