package ddc.server.service;
import ddc.server.dao.*;
import ddc.server.model.item.*;


import java.util.List;

// public class ItemService {

//     private ItemDAO itemDAO;

//     public ItemService() {
//         this.itemDAO = ItemDAO.getInstance(); // Singleton
//     }

   
//     public void createItem(Item item) {
//         validateItem(item);
//         itemDAO.addItem(item);
//     }

   
//     public void updateItem(Item updatedItem) {
//         validateItem(updatedItem);

//         Item existing = itemDAO.getItemById(updatedItem.getId());
//         if (existing == null) {
//             throw new RuntimeException("Item not found");
//         }

//         itemDAO.updateItem(updatedItem);
//     }

    
//     public void deleteItem(String itemId) {
//         Item existing = itemDAO.getItemById(itemId);
//         if (existing == null) {
//             throw new RuntimeException("Item not found");
//         }

//         itemDAO.deleteItem(Integer.parseInt(itemId));
//     }

    
//     public List<Item> getAllItems() {
//         return itemDAO.getAllItems();
//     }

    
//     public Item getItemById(String id) {
//         return itemDAO.getItemById(id);
//     }

    
//     private void validateItem(Item item) {
//         if (item.getName() == null || item.getName().isEmpty()) {
//             throw new IllegalArgumentException("Item name is required");
//         }

//         if (item.getStartingPrice() <= 0) {
//             throw new IllegalArgumentException("Start price must be > 0");
//         }
//     }
// }