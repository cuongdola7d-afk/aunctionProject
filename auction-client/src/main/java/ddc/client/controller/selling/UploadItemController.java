package ddc.client.controller.selling;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.time.LocalDate;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.util.Duration;


public class UploadItemController implements Initializable {

    @FXML private ComboBox<String> categoryComboBox;
    @FXML private VBox dynamicFieldsContainer;
    @FXML private TextField itemNameField;
    @FXML private TextArea itemDescriptionArea;
    @FXML private Button btnNext;
    @FXML private VBox step1Container;
    @FXML private VBox step2Container;
    @FXML private TextField priceField;
    @FXML private DatePicker auctionDatePicker;
    @FXML private ImageView mainImageView;
    @FXML private Button registerButton;
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

        step1Container.setVisible(true);
        step1Container.setManaged(true);

        step2Container.setVisible(false);
        step2Container.setManaged(false);

        // Mặc định lúc mới mở tab 2 thì nút phải xám (disable)
    registerButton.setDisable(true);

    // Lắng nghe khi người dùng gõ giá tiền
    priceField.textProperty().addListener((observable, oldValue, newValue) -> {
        updateRegisterButtonState();
    });

    // Lắng nghe khi người dùng chọn ngày
    auctionDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
        updateRegisterButtonState();
    });
}

    private void renderFields(String category) {
        // 1. Xóa sạch các ô cũ để không bị chồng chất
        dynamicFieldsContainer.getChildren().clear();
        dynamicTextFields.clear();

        if (category == null) return;
        switch (category) {
            case "Nghệ Thuật (e.g: Tranh, ảnh, ...)":
                addTextField("Tác giả:", "e.g: Da Vinci...");
                addTextField("Năm sáng tác:", "e.g: 2024");
                break;
            case "Xe":
                addTextField("Hãng:", "e.g: Ferrari");
                addTextField("Năm sản xuất:", "e.g: 2024...");
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
    @FXML
    private void handleNextStep() {
        // 1. Ẩn Step 1
        step1Container.setVisible(false);
        step1Container.setManaged(false);

        // 2. Hiện Step 2
        step2Container.setVisible(true);
        step2Container.setManaged(true);

        // 3. (Tùy chọn) Thêm hiệu ứng Fade cho xịn
        FadeTransition ft = new FadeTransition(Duration.millis(500), step2Container);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
}

    @FXML
    private void handleInitialize() {
        // 1. Giai đoạn Loading: Đổi chữ trên nút và đổi màu
        registerButton.setText("Đang xử lý... ⏳");
        registerButton.setDisable(true); // Khóa nút để tránh bấm lung tung
        registerButton.setStyle("-fx-background-color: #555555; -fx-text-fill: white;");

        // 2. Tạo độ trễ 2 giây (giả lập nạp dữ liệu)
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            
            // 3. Giai đoạn Done: Đổi chữ thành tích xanh và đổi màu nền xanh lá
            registerButton.setText("Thành công! ✔");
            registerButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");

            // 4. Đợi 1 giây rồi tắt cửa sổ
            PauseTransition closePause = new PauseTransition(Duration.seconds(1));
            closePause.setOnFinished(event -> {
                Stage stage = (Stage) registerButton.getScene().getWindow();
                stage.close();
            });
            closePause.play();
        });
        pause.play();
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

private void updateRegisterButtonState() {
    // Kiểm tra: giá không trống VÀ ngày đã được chọn
    boolean isPriceEntered = priceField.getText() != null && !priceField.getText().trim().isEmpty();
    boolean isDateSelected = auctionDatePicker.getValue() != null;
    boolean canProceed = isPriceEntered && isDateSelected;
    // Nếu cả 2 đều thỏa mãn thì enable nút, ngược lại thì disable
    registerButton.setDisable(!canProceed);

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
