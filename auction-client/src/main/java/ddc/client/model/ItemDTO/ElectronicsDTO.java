package ddc.client.model.ItemDTO;

public class ElectronicsDTO extends ItemGeneric<ElectronicsDTO>{
    private String brand;
    private int warrantyMonths;

    public ElectronicsDTO () {}

    //Getters
    public String getBrand() { return brand; }
    public int getWarrantyMonths() { return warrantyMonths; }

    //Setters
    public ElectronicsDTO setBrand (String brand) {
        this.brand = brand;
        return this;
    }
    
    public ElectronicsDTO setWarrantyMonths (int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
        return this;
    }
}