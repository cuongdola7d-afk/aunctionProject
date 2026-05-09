package ddc.client.model.ItemDTO;


public abstract class ItemGeneric<T extends ItemGeneric<T>> {
    private String id;
    private String itemName;
    private String category;
    private String description;
    private String sellerName;
    private String imageUrl;

    public ItemGeneric () {}

    public String getId() { return id; }
    public String getItemName() { return itemName; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getSellerName() { return sellerName; }
    public String getImageUrl() { return imageUrl; }

    protected T self () {
        return (T) this;
    }

    public T setId (String id) {
        this.id = id;
        return self();
    }

    public T setItemName (String itemName) {
        this.itemName = itemName;
        return self();
    }

    public T setCategory (String category) {
        this.category = category;
        return self();
    }

    public T setDescription (String description) {
        this.description = description;
        return self();
    }

    public T setSellerName (String sellerName) {
        this.sellerName = sellerName;
        return self();
    }

    public T setImageUrl (String ImgUrl) {
        this.imageUrl = ImgUrl;
        return self();
    }
}