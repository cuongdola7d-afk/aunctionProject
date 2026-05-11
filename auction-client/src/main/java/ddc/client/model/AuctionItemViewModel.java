package ddc.client.model;

import java.time.LocalDateTime;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AuctionItemViewModel {
    private final String auctionId;
    private final String name;
    private String price;
    private StringProperty timeLeft = new SimpleStringProperty();
    private final LocalDateTime endTime;
    private final String imagePath;
    private final String category;

    public AuctionItemViewModel(String auctionId, String name, String price, LocalDateTime endTime, String timeLeft, String imagePath, String category) {
        this.auctionId = auctionId;
        this.name = name;
        this.price = price;
        this.endTime = endTime;
        this.timeLeft.set(timeLeft);
        this.imagePath = imagePath;
        this.category = category;
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