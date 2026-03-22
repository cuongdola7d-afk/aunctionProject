package entity.item;

public class Electronics extends Item {
    private String brand;
    private int warrantyMonths;

    @Override
    public String getCategory() {
        return "Electronics";
    }
}