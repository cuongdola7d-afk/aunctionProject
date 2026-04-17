package ddc.server.model.item;

public class Vehicle extends Item{
    private final String manufacturer;
    private final int year;

    public Vehicle (Builder builder) {
        super(builder);
        this.manufacturer = builder.manufacturer;
        this.year = builder.year;
    }

    public String getManufacturer() { return manufacturer; }
    public int getYear() { return year; }

    public static class Builder extends ItemBuilder<Vehicle, Builder> {
        private String manufacturer;
        private int year;

        public Builder manufacturer (String manufacturer) {
            this.manufacturer = manufacturer;
            return this;
        }

        public Builder year (int year) {
            this.year = year;
            return this;
        }

        @Override
        public Vehicle build() {
            return new Vehicle(this);
        }
    }
}