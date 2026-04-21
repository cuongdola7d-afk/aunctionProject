package ddc.server.pattern.factory.ItemCreating;
import ddc.server.exception.ItemValidationException;
import ddc.server.model.item.Art;
import ddc.server.model.item.ItemGeneric;

public class ArtCreator extends ItemCreator {
    @Override
    public ItemGeneric createItem(ItemRequest req) throws ItemValidationException {
        // Fluent API giúp code cực gọn, không cần tạo biến tạm
        return Art.create()
                .setItemName(req.itemName)
                .setDescription(req.description)
                .setCategory(req.category)
                .setSellerName(req.sellerName)
                .setAuthor(req.author)      // Chỉ nhặt artist
                .setyearCreated(req.yearCreated) // Chỉ nhặt yearCreated
                .validate();                // Chốt chặn Exception
    }
}