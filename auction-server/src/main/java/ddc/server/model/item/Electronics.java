package ddc.server.model.item;

public class Electronics extends ItemGeneric<Electronics>{
    private String brand;
    private int warrantyMonths;

    public Electronics () {}

    //Getters
    public String getBrand() { return brand; }
    public int getWarrantyMonths() { return warrantyMonths; }

    //Setters
    public Electronics setBrand (String brand) {
        this.brand = brand;
        return this;
    }
    
    public Electronics setWarrantyMonths (int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
        return this;
    }
}