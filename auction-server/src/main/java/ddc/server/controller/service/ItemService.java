package ddc.server.controller.service;

import ddc.server.dao.ItemDAO;
import ddc.server.exception.ItemValidationException;
import ddc.server.model.item.ItemGeneric;
import ddc.server.pattern.factory.itemcreating.CreatorRegistry;
import ddc.server.pattern.factory.itemcreating.ItemCreator;
import ddc.server.pattern.factory.itemcreating.ItemRequest;

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
    // 1. Thêm "throws ItemValidationException" vào tên hàm
        public boolean createAndSaveItem(ItemRequest req) throws ItemValidationException {
            // 2. BỎ HOÀN TOÀN KHỐI TRY-CATCH
            
            // Check tên
            if (req.getItemName() == null || req.getItemName().isEmpty()) {
                throw new ItemValidationException.MissingFieldException("Ten san pham khong duoc de trong!");
            }
            
            // Check Category
            if (req.getCategory() == null || req.getCategory().isEmpty()) {
                throw new ItemValidationException.InvalidCategoryException("Category khong duoc de trong!");
            }
            
            // Bước 1: Tìm Creator (Bản thân hàm này cũng đã throws InvalidCategoryException rồi)
            ItemCreator creator = CreatorRegistry.getCreator(req.getCategory());

            // Bước 2: Tạo Object và Validate (Hàm này cũng throws MissingFieldException...)
            ItemGeneric newItem = creator.createItem(req);
            newItem.validate();
            
            // Bước 3: Lưu vào DB
            boolean isSaved = itemDAO.addItem(newItem);

            if (isSaved) {
                System.out.println("Service: Da luu san pham thanh cong!");
            }
            return isSaved;
        }

    /**
     * Lấy thông tin sản phẩm chi tiết
     */
    public ItemGeneric getItemDetails(String id) {
        return itemDAO.getItem(id);
    }
}