package ddc.server.model.item;

public class Electronics extends Item {
    private String brand;
    private int warrantyMonths;

    public Electronics() {
        setCategory("ELECTRONICS");
    }

    public Electronics(String itemName, String description, double startingPrice) {
        super(itemName, description, startingPrice);
        setCategory("ELECTRONICS");
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }
}