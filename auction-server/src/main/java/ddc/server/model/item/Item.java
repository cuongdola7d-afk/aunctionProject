package ddc.server.model.item;

import ddc.server.model.entity.Entity;

public class Item extends Entity {
    private final String id;
    private final String item;
    private final String category;
    private final String description;
    private final String seller;

    protected Item(ItemBuilder<?, ?> builder) {
        this.id = builder.id;
        this.item = builder.item;
        this.category = builder.category;
        this.description = builder.description;
        this.seller = builder.seller;
    }

    public String getId() {
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

    public static abstract class ItemBuilder<C extends Item, B extends ItemBuilder<C, B>> {
        private String id;
        private String item;
        private String category;
        private String description;
        private String seller;

        public B id(String id) {
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