package ddc.client.model;

public class AuctionItemViewModel {
    private final String name;
    private final String price;
    private final String timeLeft;
    private final String imagePath;

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