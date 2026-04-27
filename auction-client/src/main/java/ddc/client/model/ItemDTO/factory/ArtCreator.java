package ddc.client.model.ItemDTO.factory;
import ddc.client.exception.ItemValidationException;
import ddc.client.model.ItemDTO.ArtDTO;
import ddc.client.model.ItemDTO.ItemGeneric;

public class ArtCreator extends ItemCreator {
    @Override
    public ItemGeneric createItem(ItemRequest req) throws ItemValidationException {
        // Fluent API giúp code cực gọn, không cần tạo biến tạm
        return ArtDTO.create()
                .setId(req.id)
                .setItemName(req.itemName)
                .setDescription(req.description)
                .setCategory(req.category)
                .setSellerName(req.sellerName)
                .setAuthor(req.author)      // Chỉ nhặt artist
                .setyearCreated(req.yearCreated); // Chỉ nhặt yearCreated
    }
}