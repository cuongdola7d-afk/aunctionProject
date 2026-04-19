package ddc.server.model.item;

/**
 * ItemGeneric đóng vai trò là lớp hỗ trợ Builder (Fluent Interface).
 * Nó không giữ biến mà dùng 'super' để đổ dữ liệu vào lớp Item cha.
 * T giúp các lớp con (Art, Electronics, Vehicle) trả về đúng kiểu của chúng.
 */
public abstract class ItemGeneric<T extends ItemGeneric<T>> extends Item {

    public ItemGeneric() {
        super();
    }

    // Hàm bổ trợ để ép kiểu trả về chính xác cho lớp con
    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    @Override
    public T setItemName(String itemName) {
        super.setItemName(itemName);
        return self();
    }

    @Override
    public T setCategory(String category) {
        super.setCategory(category);
        return self();
    }

    @Override
    public T setDescription(String description) {
        super.setDescription(description);
        return self();
    }

    @Override
    public T setStartingPrice(double startingPrice) {
        super.setStartingPrice(startingPrice);
        return self();
    }

    @Override
    public T setSellerName(String sellerName) {
        super.setSellerName(sellerName);
        return self();
    }

    // Nếu lớp Entity/Item của bạn có ID, hãy thêm hàm này
    public T setItemId(String id) {
        this.setId(id); // Giả định lớp cha có setId
        return self();
    }
}