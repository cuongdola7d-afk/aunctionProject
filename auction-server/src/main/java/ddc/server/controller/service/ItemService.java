package ddc.server.controller.service;

import ddc.server.dao.ItemDAO;
import ddc.server.exception.ItemValidationException;
import ddc.server.model.item.ItemGeneric;
import ddc.server.pattern.factory.ItemCreating.CreatorRegistry;
import ddc.server.pattern.factory.ItemCreating.ItemCreator;
import ddc.server.pattern.factory.ItemCreating.ItemRequest;

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
    public String createAndSaveItem(ItemRequest req) {
        try {
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

            // Bước 2: Dùng Factory để tạo ra đối tượng Item chuẩn
            ItemGeneric newItem = creator.createItem(req);
            newItem.validate();
            System.out.println(newItem);
            System.out.println("Tao object thanh cong");
            
            //Bước 3: Sau khi có Object xịn, gọi DAO để "bốc" nó vào SQL
            String id = itemDAO.addItem(newItem);

            if (!id.isEmpty()) {
                System.out.println("Service: Da luu san pham " + newItem.getItemName() + " thanh cong!");
            }
            return id;
            

        } catch (Exception e) {
            System.err.println("Service Loi: " + e.getMessage());
            return null;
        }
    }

    /**
     * Lấy thông tin sản phẩm chi tiết
     */
    public ItemGeneric getItemDetails(String id) {
        return itemDAO.getItem(id);
    }
}