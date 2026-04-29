package ddc.server.controller.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import ddc.server.dao.ItemDAO;
import ddc.server.exception.ItemValidationException;
import ddc.server.model.item.ItemGeneric;
import ddc.server.pattern.factory.CreatorRegistry;
import ddc.server.pattern.factory.ItemCreator;
import ddc.server.pattern.factory.ItemRequest;

public class ItemService {
    private static final Logger LOGGER = Logger.getLogger(ItemService.class.getName());

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
    public String createAndSaveItem(ItemRequest req) throws ItemValidationException{
            if (req.getItemName() == null || req.getItemName().isEmpty()) {
                throw new ItemValidationException.MissingFieldException("Tên sản phẩm không được để trống!");
            }
            // Bước 1: Tìm xưởng sản xuất dựa trên type
            System.out.println(">>> Đang kiem tra Category: " + req.getCategory());
            ItemCreator creator = CreatorRegistry.getCreator(req.getCategory());
            
            if (creator == null) {
                System.out.println("Khong tim thay creator cho loai: " + req.getCategory());
                return null;
            }
            
            // Check Category
            if (req.getCategory() == null || req.getCategory().isEmpty()) {
                throw new ItemValidationException.InvalidCategoryException("Category khong duoc de trong!");
            }

            if (isBlank(req.getItemName())) {
                throw new ItemValidationException.MissingFieldException("Ten san pham khong duoc de trong.");
            }

            if (isBlank(req.getCategory())) {
                throw new ItemValidationException.InvalidCategoryException("Category khong duoc de trong.");
            }


            ItemGeneric newItem = creator.createItem(req);
            newItem.validate();

            String id = itemDAO.addItem(newItem);
            if (isBlank(id)) {
                LOGGER.log(Level.WARNING, "Khong the luu san pham: {0}", newItem.getItemName());
                return null;
            }
            return id;
        }


    public ItemGeneric getItemDetails(String id) {
        if (isBlank(id)) {
            return null;
        }
        return itemDAO.getItem(id.trim());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

