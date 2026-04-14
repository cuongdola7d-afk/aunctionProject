package ddc.server.service;
import ddc.server.dao.*;
import ddc.server.exception.ItemValidationException;
import ddc.server.model.item.*;


import java.util.List;

public class ItemService {

    private ItemDAO itemDAO;

    public ItemService() {
        this.itemDAO = ItemDAO.getInstance(); // Singleton
    }

   
    public void createItem(Item item) throws ItemValidationException {
        validateProduct(item.getName(),item.getStartingPrice());
        itemDAO.addItem(item);
    }

   
    public void updateItem(Item updatedItem) throws ItemValidationException {
        validateProduct(updatedItem.getName(),updatedItem.getStartingPrice());

        Item existing = itemDAO.getItemById(updatedItem.getId());
        if (existing == null) {
            throw new RuntimeException("Item not found");
        }

        itemDAO.updateItem(updatedItem);
    }

    
    public void deleteItem(String itemId) {
        Item existing = itemDAO.getItemById(itemId);
        if (existing == null) {
            throw new RuntimeException("Item not found");
        }

        itemDAO.deleteItem(Integer.parseInt(itemId));
    }

    
    public List<Item> getAllItems() {
        return itemDAO.getAllItems();
    }

    
    public Item getItemById(String id) {
        return itemDAO.getItemById(id);
    }


    public void validateProduct(String name, double price) throws ItemValidationException {
    if (name == null || name.isEmpty()) {
        throw new ItemValidationException.MissingFieldException("Tên sản phẩm không được để trống!");
    }
    if (price <= 0) {
        throw new ItemValidationException.InvalidPriceException("Giá khởi điểm phải lớn hơn 0!");
    }
}
}