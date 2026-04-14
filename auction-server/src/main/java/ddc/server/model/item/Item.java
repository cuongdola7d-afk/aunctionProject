package ddc.server.model.item;

import java.time.LocalDateTime;

public class Item {
    private int id;
    private String item;
    private String category;
    private String description;
    private String seller;
    private double startingPrice;
    private double currentPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    protected Item(ItemBuilder builder) {
        this.id = builder.id;
        this.item = builder.item;
        this.category = builder.category;
        this.description = builder.description;
        this.seller = builder.seller;
        this.startingPrice = builder.startingPrice;
        this.currentPrice = builder.currentPrice;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.status = builder.status;
    }

    public int getId() {
        return id;
    }

    public String getitem() {
        return item;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getSeller() {
        return seller;
    }

    public double getStartingPrice() {
        return startingPrice;
    }

    public double getcurrentPrice() {
        return currentPrice;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public static abstract class ItemBuilder<C extends Item, B extends ItemBuilder<C, B>> {
        private int id;
        private String item;
        private String category;
        private String description;
        private String seller;
        private double startingPrice;
        private double currentPrice;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String status;

        public B id(int id) {
            this.id = id;
            return self();
        }

        public B item(String item) {
            this.item = item;
            return self();
        }

        public B category(String category) {
            this.category = category;
            return self();
        }

        public B description(String description) {
            this.description = description;
            return self();
        }

        public B seller(String seller) {
            this.seller = seller;
            return self();
        }

        public B startingPrice(double startingPrice) {
            this.startingPrice = startingPrice;
            return self();
        }

        public B startTime(LocalDateTime startTime) {
            this.startTime = startTime;
            return self();
        }

        public B endTime(LocalDateTime endTime) {
            this.endTime = endTime;
            return self();
        }

        public B status(String status) {
            this.status = status;
            return self();
        }

        protected B self() {
            return (B) this;
        }

        public abstract C build();
    }

    public static class Builder extends ItemBuilder<Item, Builder> {
        @Override
        public Item build() {
            return new Item(this);
        }
    }
    
}