package ddc.server.service;

import ddc.server.dao.ItemDAO;
import ddc.server.model.item.Item;
import ddc.server.pattern.factory.ItemCreator.*;

public class ItemService {
    private final ItemDAO itemDAO;

    public ItemService() {
        this.itemDAO = new ItemDAO();
    }

    /**
     * Quy trình tạo sản phẩm: 
     * 1. Nhận Request từ Controller
     * 2. Tìm Factory phù hợp
     * 3. Tạo và Validate Object
     * 4. Lưu vào Database thông qua DAO
     */
    public boolean createAndSaveItem(ItemRequest req) {
        try {
            // Bước 1: Tìm xưởng sản xuất dựa trên type
            ItemCreator creator = CreatorRegistry.getCreator(req.getType());
            
            if (creator == null) {
                System.out.println("Lỗi: Không tìm thấy xưởng sản xuất cho loại: " + req.getType());
                return false;
            }

            // Bước 2: Dùng Factory để tạo ra đối tượng Item chuẩn
            Item newItem = creator.createItem(req);

            // Bước 3: Sau khi có Object xịn, gọi DAO để "bốc" nó vào SQL
            boolean isSaved = itemDAO.addItem(newItem);

            if (isSaved) {
                System.out.println("Service: Đã lưu sản phẩm " + newItem.getItemName() + " thành công!");
            }
            return isSaved;

        } catch (Exception e) {
            System.err.println("Service Lỗi: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy thông tin sản phẩm chi tiết
     */
    public Item getItemDetails(String id) {
        return itemDAO.getItem(id);
    }
}