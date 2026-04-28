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

    // Tao va luu san pham sau khi validate request.
    public String createAndSaveItem(ItemRequest req) throws ItemValidationException {
        if (req == null) {
            throw new ItemValidationException.MissingFieldException("Request san pham khong hop le.");
        }

        if (isBlank(req.getItemName())) {
            throw new ItemValidationException.MissingFieldException("Ten san pham khong duoc de trong.");
        }

        if (isBlank(req.getCategory())) {
            throw new ItemValidationException.InvalidCategoryException("Category khong duoc de trong.");
        }

        ItemCreator creator = CreatorRegistry.getCreator(req.getCategory());
        if (creator == null) {
            throw new ItemValidationException.InvalidCategoryException("Category khong duoc ho tro.");
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
