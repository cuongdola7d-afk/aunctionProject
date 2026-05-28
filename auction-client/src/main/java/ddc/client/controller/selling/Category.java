package ddc.client.controller.selling;

import ddc.client.model.ItemDTO.ArtDTO;
import ddc.client.model.ItemDTO.ElectronicsDTO;
import ddc.client.model.ItemDTO.GeneralDTO;
import ddc.client.model.ItemDTO.ItemGeneric;
import ddc.client.model.ItemDTO.VehicleDTO;
import javafx.scene.control.TextField;

public enum Category {

    GENERAL("Chung") {
        @Override
        public void renderUI(FieldBuilder builder) {
        }

        @SuppressWarnings("rawtypes")
        @Override
        public ItemGeneric getItemData(String itemName, String description, String sellerName) {
            return new GeneralDTO()
                    .setItemName(itemName)
                    .setDescription(description)
                    .setCategory("GENERAL")
                    .setSellerName(sellerName);
        }
    },
    
    // 1. CỤC LOGIC CỦA NGHỆ THUẬT
    ART("Nghệ thuật") {
        private TextField authorField;
        private TextField yearCreatedField;

        @Override
        public void renderUI(FieldBuilder builder) {
            this.authorField = builder.add("Tác giả: ","Nhập tên tác giả.");
            this.yearCreatedField = builder.add("Năm sáng tác: ","Nhập năm sáng tác.");
        }

        @SuppressWarnings("rawtypes")
        @Override
        public ItemGeneric getItemData(String itemName, String description, String sellerName) {
            return new ArtDTO()
                    .setItemName(itemName)
                    .setDescription(description)
                    .setCategory("ART")
                    .setSellerName(sellerName)
                    .setAuthor(authorField.getText())
                    .setyearCreated(Integer.parseInt(yearCreatedField.getText()));
        }
    },

    ELECTRONICS("Điện tử") {
    private TextField BrandField;
    private TextField warrantyField;

    @Override
    public void renderUI(FieldBuilder builder) {
        // Tạo các ô nhập liệu đặc thù cho Điện tử
        this.BrandField = builder.add("Hãng sản xuất: ", "Nhập hãng sản xuất (VD: Apple, Samsung).");
        this.warrantyField = builder.add("Thời gian bảo hành (tháng): ", "Nhập số tháng bảo hành.");
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ItemGeneric getItemData(String itemName, String description, String sellerName) {
        // Trả về đối tượng ElectronicsDTO với các thông tin đã nhập
        return new ElectronicsDTO()
                .setItemName(itemName)
                .setDescription(description)
                .setCategory("ELECTRONICS")
                .setSellerName(sellerName)
                .setBrand(BrandField.getText())
                .setWarrantyMonths(Integer.parseInt(warrantyField.getText())); // Hoặc parse sang Integer tùy theo DTO của bạn
    }
},

    // 2. CỤC LOGIC CỦA PHƯƠNG TIỆN
    VEHICLE("Phương tiện") {
        private TextField manufacturerField;
        private TextField yearField;

        @Override
        public void renderUI(FieldBuilder builder) {
            this.manufacturerField = builder.add("Nhà sản xuất: ", "Nhập tên nhà sản xuất.");
            this.yearField = builder.add("Năm sản xuất: ", "Nhập tên năm sản xuất.");
        }

        @SuppressWarnings("rawtypes")
        @Override
        public ItemGeneric getItemData(String itemName, String description, String sellerName) {
            return new VehicleDTO()
                    .setItemName(itemName)
                    .setDescription(description)
                    .setCategory("VEHICLE")
                    .setSellerName(sellerName)
                    .setManufacturer(manufacturerField.getText())
                    .setYear(Integer.parseInt(yearField.getText()));
        }
    };

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public abstract void renderUI(FieldBuilder builder);

    @SuppressWarnings("rawtypes")
    public abstract ItemGeneric getItemData(String itemName, String description, String sellerName);

    public static Category fromDisplayName(String itemName) {
        for (Category cat : values()) {
            if (cat.getDisplayName().equals(itemName)) return cat;
        }
        return null;
    }

    public interface FieldBuilder {
        TextField add(String labelText, String promptText);
    }
}