package ddc.client.controller.selling;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UploadItemController implements Initializable {

    @FXML private ComboBox<String> categoryComboBox; // ComboBox của bạn
    @FXML private VBox dynamicFieldsContainer;
    @FXML private TextField itemNameField;
    @FXML private TextArea itemDescriptionArea;
    @FXML private Button btnNext;
    private List<TextField> dynamicTextFields = new ArrayList<>();

    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Nạp dữ liệu
        categoryComboBox.getItems().addAll("Nghệ Thuật (e.g: Tranh, ảnh, ...)", "Xe", "Đồ điện tử");

        // Lắng nghe sự kiện đổi giá trị
        categoryComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            renderFields(newVal);
        });

        itemNameField.textProperty().addListener((o, old, newVal) -> updateNextButtonState());
        itemDescriptionArea.textProperty().addListener((o, old, newVal) -> updateNextButtonState());
        categoryComboBox.valueProperty().addListener((o, old, newVal) -> updateNextButtonState());
}

    private void renderFields(String category) {
        // 1. Xóa sạch các ô cũ để không bị chồng chất
        dynamicFieldsContainer.getChildren().clear();
        dynamicTextFields.clear();

        if (category == null) return;
        switch (category) {
            case "Nghệ Thuật (e.g: Tranh, ảnh, ...)":
                addTextField("Tác giả:", "e.g: Canvas, Gỗ...");
                addTextField("Năm sáng tác:", "e.g: 2024");
                break;
            case "Xe":
                addTextField("Hãng:", "e.g:Ferrari");
                addTextField("Năm sản xuất:", "eg:2024...");
                break;
            case "Đồ điện tử":
                addTextField("Hãng sản xuất:", "e.g: Apple, Samsung...");
                addTextField("Thời gian bảo hành:", "e.g: 12 tháng");
                break;
        }

        updateNextButtonState();

        Platform.runLater(() -> {
            Stage stage = (Stage) dynamicFieldsContainer.getScene().getWindow();
            if (stage != null) {
                stage.sizeToScene();
            }
        });
    }

    private void updateNextButtonState() {
        // 1. Kiểm tra các ô cố định
        boolean isBasicInfoValid = !itemNameField.getText().trim().isEmpty() 
                                && !itemDescriptionArea.getText().trim().isEmpty() 
                                && categoryComboBox.getValue() != null;

        // 2. Kiểm tra tất cả các ô động (phải điền hết)
        boolean areExtraFieldsValid = true;
        for (TextField tf : dynamicTextFields) {
            if (tf.getText().trim().isEmpty()) {
                areExtraFieldsValid = false;
                break;
            }
        }

        // 3. Quyết định bật hay tắt nút
        boolean canProceed = isBasicInfoValid && areExtraFieldsValid;
        
        btnNext.setDisable(!canProceed);

        // Cập nhật màu sắc
        if (!canProceed) {
            btnNext.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666;");
        } else {
            btnNext.setStyle("-fx-background-color: #00008b; -fx-cursor: hand;");
        }
}

    private void addTextField(String labelText, String promptText) {
        Label label = new Label(labelText);
        TextField textField = new TextField();
        textField.setPromptText(promptText);

            // Lưu vào danh sách để quản lý
        dynamicTextFields.add(textField);
        
        // Mỗi khi gõ chữ vào ô này, gọi hàm kiểm tra để bật/tắt nút
        textField.textProperty().addListener((obs, oldVal, newVal) -> updateNextButtonState());

        dynamicFieldsContainer.getChildren().addAll(label, textField);
    }
}
