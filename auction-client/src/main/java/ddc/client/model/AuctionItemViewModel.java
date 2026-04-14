package ddc.client.model;

public class AuctionItemViewModel {
    private final String auctionId;
    private final String name;
    private String price;
    private final String timeLeft;
    private final String imagePath;
    private final String category;

    public AuctionItemViewModel(String auctionId, String name, String price, String timeLeft, String imagePath, String category) {
        this.auctionId = auctionId;
        this.name = name;
        this.price = price;
        this.timeLeft = timeLeft;
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

    public String getTimeLeft() {
        return timeLeft;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getCategory() {
        return category;
    }
}