package ddc.server.model.item;

public class Electronics extends Item {
    private String brand;
    private int warrantyMonths;

    public Electronics(String id, String name, String desc, double price, String brand, int warrantyMonths) {
        super(name, desc, price);
        this.brand = brand;
        this.warrantyMonths = warrantyMonths;
    }

    @Override
    public String getCategory() {
        return "Electronics";
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