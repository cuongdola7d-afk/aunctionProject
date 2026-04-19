package ddc.server.model.item;

public class Electronics extends Item {
    private final String brand;
    private final int warrantyMonths;

    public Electronics(Builder builder) {
        super(builder);
        this.brand = builder.brand;
        this.warrantyMonths = builder.warrantyMonths;
        setCategory("ELECTRONICS");
    }

    public String getBrand() {
        return brand;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    public static class Builder extends ItemBuilder<Electronics, Builder> {
        private String brand;
        private int warrantyMonths;

        public Builder() {
            category("ELECTRONICS");
        }

        public Builder brand(String brand) {
            this.brand = brand;
            return this;
        }

        public Builder warrantyMonths(int warrantyMonths) {
            this.warrantyMonths = warrantyMonths;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public Electronics build() {
            return new Electronics(this);
        }
    }
}