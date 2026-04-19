package ddc.server.model.item;

import ddc.server.model.entity.Entity;

public class Item extends Entity {
    private final String itemName;
    private final String category;
    private final String description;
    private final String sellerName;

    protected Item(ItemBuilder<?, ?> builder) {
        this.itemName = builder.itemName;
        this.category = builder.category;
        this.description = builder.description;
        this.sellerName = builder.sellerName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getSellerName() {
        return sellerName;
    }

    public static abstract class ItemBuilder<C extends Item, B extends ItemBuilder<C, B>> {
        private String id;
        private String itemName;
        private String category;
        private String description;
        private String sellerName;

        public B id(String id) {
            this.id = id;
            return self();
        }

        public B item(String item) {
            this.itemName = item;
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
            this.sellerName = seller;
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