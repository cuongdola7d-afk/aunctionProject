package ddc.server.pattern.factory.ItemCreator;
import ddc.server.model.item.*;
import ddc.server.exception.*;

public class ArtCreator extends ItemCreator {
    @Override
    public Item createItem(ItemRequest req) throws ItemValidationException {
        // Fluent API giúp code cực gọn, không cần tạo biến tạm
        return (Item) Art.create()
                .setItemName(req.name)
                .setDescription(req.description)
                .setStartingPrice(req.startingPrice)
                .setAuthor(req.artist)      // Chỉ nhặt artist
                .setyearCreated(req.yearCreated) // Chỉ nhặt yearCreated
                .validate();                // Chốt chặn Exception
    }
}