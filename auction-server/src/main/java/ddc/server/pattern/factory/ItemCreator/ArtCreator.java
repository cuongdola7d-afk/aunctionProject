package ddc.server.pattern.factory.ItemCreator;
import ddc.server.model.item.*;

public class ArtCreator extends ItemCreator {
    @Override
    public Item createItem(ItemRequest req) {
        // Bạn lấy dữ liệu trực tiếp từ các thuộc tính của req
        return new Art(
            req.name,
            req.description, 
            req.startingPrice, 
            req.artist, 
            req.yearCreated
        );
    }
}