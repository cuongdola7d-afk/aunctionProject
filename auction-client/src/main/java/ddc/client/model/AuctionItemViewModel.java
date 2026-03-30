package ddc.client.model;

public class AuctionItemViewModel {
    private String name;
    private String price;
    private String timeLeft;
    private String imagePath;

    public AuctionItemViewModel(String name, String price, String timeLeft, String imagePath) {
        this.name = name;
        this.price = price;
        this.timeLeft = timeLeft;
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getTimeLeft() {
        return timeLeft;
    }

    public String getImagePath() {
        return imagePath;
    }
}