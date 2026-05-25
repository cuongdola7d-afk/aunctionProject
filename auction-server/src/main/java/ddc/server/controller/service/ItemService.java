package ddc.server.controller.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ddc.server.config.GsonConfig;
import ddc.server.dao.ItemDAO;
import ddc.server.exception.ItemValidationException;
import ddc.server.model.item.ItemGeneric;
import ddc.server.pattern.factory.CreatorRegistry;
import ddc.server.pattern.factory.ItemCreator;
import ddc.server.pattern.factory.ItemRequest;

public class ItemService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemService.class);

    private final ItemDAO itemDAO;
    private final Gson gson;

    public ItemService() {
        this(new ItemDAO(), GsonConfig.newGson());
    }

    ItemService(ItemDAO itemDAO) {
        this(itemDAO, GsonConfig.newGson());
    }

    ItemService(ItemDAO itemDAO, Gson gson) {
        this.itemDAO = itemDAO;
        this.gson = gson;
    }

    /**
     * Quy trình tạo sản phẩm:
     * 1. Nhận Request từ Controller
     * 2. Tìm Factory phù hợp
     * 3. Tạo và Validate Object
     * 4. Lưu vào Database thông qua DAO
     */
    public String createAndSaveItem(ItemRequest req) throws ItemValidationException {
        if (req.getItemName() == null || req.getItemName().isEmpty()) {
            throw new ItemValidationException.MissingFieldException("Tên sản phẩm không được để trống!");
        }
        // Bước 1: Tìm xưởng sản xuất dựa trên type
        LOGGER.info("Dang kiem tra Category: {}", req.getCategory());
        ItemCreator creator = CreatorRegistry.getCreator(req.getCategory());

        if (creator == null) {
            LOGGER.warn("Khong tim thay creator cho loai: {}", req.getCategory());
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
            LOGGER.warn("Khong the luu san pham: {}", newItem.getItemName());
            return null;
        }
        LOGGER.debug("Luu san pham thanh cong!");
        return id;
    }

    public String processUploadAndSave(String itemJson, byte[] imageData) throws ItemValidationException {
        ItemRequest itemReq = gson.fromJson(itemJson, ItemRequest.class);

        // 2. Nếu có ảnh, gọi Cloudinary để lấy URL thật
        if (imageData != null && imageData.length > 0) {
            String cloudUrl = CloudinaryService.uploadBytes(imageData);
            if (cloudUrl != null) {
                itemReq.setImageUrl(cloudUrl);
            }
        }

        // 3. Gọi hàm tạo và lưu cũ đã có của bạn
        return createAndSaveItem(itemReq);
    }

    public ItemGeneric getItemDetails(String id) {
        if (isBlank(id)) {
            return null;
        }
        return itemDAO.getItem(id);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
