// package ddc.server.service;
// import ddc.server.dao.*;
// import ddc.server.model.item.*;


// import java.util.List;

// public class ItemService {

//     private ItemDAO itemDAO;

//     public ItemService() {
//         this.itemDAO = ItemDAO.getInstance(); // Singleton
//     }

   
//     public void createItem(ItemGeneric item) {
//         validateItem(item);
//         itemDAO.addItem(item);
//     }

   
//     public void updateItem(ItemGeneric updatedItem) {
//         validateItem(updatedItem);

//         ItemGeneric existing = itemDAO.getItemById(updatedItem.getId());
//         if (existing == null) {
//             throw new RuntimeException("Item not found");
//         }

//         itemDAO.updateItem(updatedItem);
//     }

    
//     public void deleteItem(String itemId) {
//         ItemGeneric existing = itemDAO.getItemById(itemId);
//         if (existing == null) {
//             throw new RuntimeException("Item not found");
//         }

//         itemDAO.deleteItem(Integer.parseInt(itemId));
//     }

    
//     public List<ItemGeneric> getAllItems() {
//         return itemDAO.getAllItems();
//     }

    
//     public ItemGeneric getItemById(String id) {
//         return itemDAO.getItemById(id);
//     }

    
//     private void validateItem(ItemGeneric item) {
//         if (item.getName() == null || item.getName().isEmpty()) {
//             throw new IllegalArgumentException("Item name is required");
//         }

//         if (item.getStartingPrice() <= 0) {
//             throw new IllegalArgumentException("Start price must be > 0");
//         }
//     }
// }