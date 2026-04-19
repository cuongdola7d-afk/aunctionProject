package ddc.server.model.item;

import ddc.server.model.entity.Entity;

public abstract class Item extends Entity {
    private String itemName;
    private String description;
    private String category;
    private String sellerName;
    private double startingPrice;
    private double currentPrice;

    protected Item(ItemBuilder<?, ?> builder) {
        this.itemName = builder.itemName;
        this.description = builder.description;
        this.category = builder.category;
        this.sellerName = builder.sellerName;
        this.startingPrice = builder.startingPrice;
        this.currentPrice = builder.startingPrice;
    }

    protected Item(String itemName, String description, double startingPrice) {
        this.itemName = itemName;
        this.description = description;
        this.startingPrice = startingPrice;
        this.currentPrice = startingPrice;
    }

    public String getName() {
        return itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(double startingPrice) {
        this.startingPrice = startingPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public String getCategory() {
        return category;
    }

    protected void setCategory(String category) {
        this.category = category;
    }

    public static abstract class ItemBuilder<C extends Item, B extends ItemBuilder<C, B>> {
        private String itemName;
        private String description;
        private String category;
        private String sellerName;
        private double startingPrice;

        public B itemName(String itemName) {
            this.itemName = itemName;
            return self();
        }

        public B item(String itemName) {
            this.itemName = itemName;
            return self();
        }

        public B description(String description) {
            this.description = description;
            return self();
        }

        public B category(String category) {
            this.category = category;
            return self();
        }

        public B sellerName(String sellerName) {
            this.sellerName = sellerName;
            return self();
        }

        public B seller(String sellerName) {
            this.sellerName = sellerName;
            return self();
        }

        public B startingPrice(double startingPrice) {
            this.startingPrice = startingPrice;
            return self();
        }

        protected abstract B self();

        public abstract C build();
    }
}
