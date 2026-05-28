package ddc.client.controller.selling;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddc.client.config.ClientContext;
import ddc.client.config.GsonConfig;
import ddc.client.exception.ItemValidationException;
import ddc.client.model.AuctionDTO;
import ddc.client.model.ItemDTO.ItemGeneric;
import ddc.client.model.ItemDTO.factory.CreatorRegistry;
import ddc.client.model.ItemDTO.factory.ItemRequest;
import ddc.client.model.Request;
import ddc.client.network.RequestToServer;
import ddc.client.network.UserSession;
import ddc.client.network.response.AddItemResponse;
import ddc.client.network.response.BaseResponse;
import ddc.client.network.response.GetItemResponse;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class UploadItem implements Initializable {

    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    private VBox dynamicCategoryContainer, step1Container, step2Container;
    @FXML
    private TextField itemNameField, priceField, endTimeField, startTimeField;
    @FXML
    private TextArea itemDescriptionArea;
    @FXML
    private Button btnNext;
    @FXML
    private DatePicker auctionEndPicker, auctionStartPicker;
    @FXML
    private Label nameErrorLabel, desErrorLabel;

    @FXML
    private ImageView imgProduct;

    private File selectedFile;
    @FXML
    private Button registerButton, uploadBtn, removeImageBtn;

    // Bảng chứa TextField và ErrorLabel thêm vào
    private final List<TextField> dynamicTextFields = new ArrayList<>();
    private final List<Label> dynamicErrorLabels = new ArrayList<>();
    private static final String INTEGER_FIELD = "INTEGER";

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadItem.class);

    private Category currentCat;
    private final String currentUserUsername = UserSession.getInstance().getUsername();

    private final Gson gson = GsonConfig.newGson();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Nạp danh mục
        for (Category cat : Category.values()) {
            categoryComboBox.getItems().add(cat.getDisplayName());
        }

        categoryComboBox.setOnAction(event -> {
            dynamicCategoryContainer.getChildren().clear();
            dynamicTextFields.clear();
            dynamicErrorLabels.clear();
            String selected = categoryComboBox.getValue();

            // Tra cứu ra đúng Enum
            currentCat = Category.fromDisplayName(selected);
            if (currentCat != null) {
                currentCat.renderUI(this::addTextField);
            }

            Platform.runLater(() -> {
                Stage stage = (Stage) dynamicCategoryContainer.getScene().getWindow();
                if (stage != null) {
                    stage.sizeToScene();
                }
            });
        });
        // Bật step 1 tắt step 2
        setUpVisibleTrue(step1Container);
        setUpVisibleFalse(step2Container);

        // Theo dõi nút step 2
        priceField.textProperty().addListener((o, old, newVal) -> {
            updateRegisterButtonState();
        });
        auctionEndPicker.valueProperty().addListener((o, old, newVal) -> {
            updateRegisterButtonState();
        });
        endTimeField.textProperty().addListener((o, old, nemVal) -> {
            updateRegisterButtonState();
        });
        setImageSelectedState(false);

        // Không cho DatePicker editable
        disableDatePickerTextInput(auctionEndPicker);
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleNextStep() {
        boolean hasError = false;
        // Kiểm tra Danh mục (ComboBox)
        if (categoryComboBox.getValue() == null) {
            categoryComboBox.setStyle("-fx-border-color: red;");
            hasError = true;
            categoryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    categoryComboBox.setStyle("");
                }
            });
        } else {
            categoryComboBox.setStyle("");
        }

        // Kiểm tra Tên sản phẩm
        if (itemNameField.getText().trim().isEmpty()) {
            showFieldError(itemNameField, nameErrorLabel, "Tên sản phẩm không được để trống.");
            hasError = true;
        }

        // Kiểm tra mô tả
        if (itemDescriptionArea.getText().trim().isEmpty()) {
            showFieldError(itemDescriptionArea, desErrorLabel, "Mô tả không được để trống.");
            hasError = true;
        }

        // --- PHẦN 2: KIỂM TRA CÁC Ô ĐỘNG (Dùng vòng lặp) ---
        for (int i = 0; i < dynamicTextFields.size(); i++) {
            TextField textField = dynamicTextFields.get(i);
            Label errorLabel = dynamicErrorLabels.get(i);
            String value = textField.getText().trim();

            if (value.isEmpty()) {
                // Lỗi 1: Trống dữ liệu
                showFieldError(textField, errorLabel, "Thông tin này là bắt buộc.");
                hasError = true;
            } else if (INTEGER_FIELD.equals(textField.getUserData())) {
                // Lỗi 2: Sai định dạng số
                try {
                    Integer.valueOf(value);
                } catch (NumberFormatException e) {
                    showFieldError(textField, errorLabel, "Thông tin này chỉ chứa số nguyên.");
                    hasError = true;
                }
            }
        }
        // Có lỗi thì không chạy
        if (hasError) {
            return;
        }

        setUpVisibleFalse(step1Container);
        setUpVisibleTrue(step2Container);

        FadeTransition ft = new FadeTransition(Duration.millis(500), step2Container);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    @FXML
    private void handleSelectImage(MouseEvent event) {
        if (event != null) {
            event.consume();
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh sản phẩm");

        // Lọc chỉ hiện các định dạng ảnh
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));

        // Mở cửa sổ chọn file
        Stage stage = (Stage) imgProduct.getScene().getWindow();
        selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            // 1. Hiển thị ảnh lên giao diện cho người dùng xem
            Image image = new Image(selectedFile.toURI().toString(), 450, 200, true, true);
            imgProduct.setImage(image);
            setImageSelectedState(true);
            updateRegisterButtonState();
            imgProduct.setPreserveRatio(true);

            // Ép ảnh không được vượt quá chiều rộng/cao của khung nét đứt
            imgProduct.setFitWidth(450);
            imgProduct.setFitHeight(180);

            LOGGER.info("Da chon file: {}", selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleRemoveImage(MouseEvent event) {
        if (event != null) {
            event.consume();
        }

        selectedFile = null;
        imgProduct.setImage(null);
        setImageSelectedState(false);
        updateRegisterButtonState();
    }

    @FXML
    private void handleInitialize() {
        try {
            double startingPrice = Double.parseDouble(priceField.getText());

            // Kiểm tra giá hợp lệ
            if (startingPrice <= 0)
                throw new ItemValidationException("Giá phải > 0");
            // Kiểm tra giá phải là số nguyên
            if (startingPrice != Math.floor(startingPrice))
                throw new ItemValidationException("Giá khởi điểm phải là số nguyên!");
            // kiểm tra ngày hợp lệ
            if (auctionEndPicker.getValue() == null) {
                throw new ItemValidationException.InvalidDurationException("Vui lòng chọn ngày kết thúc!");
            }
            // Kiểm tra nếu ngày chọn là ngày trong quá khứ
            if (auctionEndPicker.getValue().isBefore(LocalDate.now())) {
                throw new ItemValidationException.InvalidDurationException("Ngày kết thúc không được ở quá khứ!");
            }

            LocalDateTime startDateTime = getAuctionStartTime();
            LocalDateTime endDateTime = getAuctionEndTime();

            if (!endDateTime.isAfter(startDateTime)) {
                throw new ItemValidationException.InvalidDurationException(
                        "Thời gian kết thúc phải sau thời gian bắt đầu!");
            }

            String itemName = itemNameField.getText();
            String description = itemDescriptionArea.getText();
            String sellerName = currentUserUsername;

            registerButton.setText("Đang xử lý... ");
            registerButton.setDisable(true); // Khóa nút để tránh bấm lung tung
            registerButton.setStyle("-fx-background-color: #555555; -fx-text-fill: white;");

            Task<Boolean> uploadTask = new Task<>() {
                @Override
                protected Boolean call() throws Exception {
                    // 0. Chuẩn bị dữ liệu ảnh
                    byte[] imageBytes = null;
                    String uniqueFileName = "";

                    if (selectedFile != null) {
                        // Đọc ảnh thành mảng byte
                        imageBytes = java.nio.file.Files.readAllBytes(selectedFile.toPath());

                        // Tạo tên file duy nhất để gửi cho Server lưu vào DB
                        String extension = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
                        uniqueFileName = java.util.UUID.randomUUID().toString() + extension;
                    }

                    // 1. Tạo đối tượng Item và gán tên file cho nó
                    ItemGeneric item = currentCat.getItemData(itemName, description, sellerName);
                    item.setImageUrl(uniqueFileName); // Gán cái tên file "abc.jpg" vào đây

                    // 2. GỬI DỮ LIỆU TÁCH BIỆT
                    String addedItemJson = RequestToServer.sendRequestWithImage(
                            new Request().setAction("ADD_ITEM").setData(item),
                            imageBytes);
                    AddItemResponse addedRes = gson.fromJson(addedItemJson, AddItemResponse.class);

                    if (addedRes == null || !addedRes.getStatus().contains("SUCCESS"))
                        return false;

                    // 2. GET_ITEM (để lấy đầy đủ thông tin/ID)
                    String getItemJson = RequestToServer
                            .sendRequest(new Request().setAction("GET_ITEM").setData(addedRes.getId()));
                    GetItemResponse gottenRes = gson.fromJson(getItemJson, GetItemResponse.class);

                    if (gottenRes == null || !gottenRes.getStatus().contains("SUCCESS"))
                        return false;

                    // Parse item vừa lấy về
                    ItemRequest gottenItemReq = gson.fromJson(gottenRes.getItemJson(), ItemRequest.class);
                    @SuppressWarnings("rawtypes")
                    ItemGeneric gottenItem = CreatorRegistry.getCreator(gottenItemReq.getCategory())
                            .createItem(gottenItemReq);

                    // 3. CREATE_AUCTION
                    AuctionDTO auction = new AuctionDTO()
                            .setItem(gottenItem)
                            .setCurrentPrice(startingPrice)
                            .setStartingPrice(startingPrice)
                            .setStartTime(startDateTime)
                            .setEndTime(endDateTime);

                    String createAuctionJson = RequestToServer
                            .sendRequest(new Request().setAction("CREATE_AUCTION").setData(auction));
                    BaseResponse finalRes = gson.fromJson(createAuctionJson, BaseResponse.class);

                    return finalRes != null && finalRes.getStatus().contains("SUCCESS");
                }
            };

            // --- BƯỚC 4: XỬ LÝ KẾT QUẢ (Tự động quay về UI Thread) ---
            uploadTask.setOnSucceeded(e -> {
                boolean isSuccess = uploadTask.getValue();
                if (isSuccess) {
                    LOGGER.info("Đăng tải thành công!");
                    registerButton.setText("Thành công! ✔");
                    registerButton
                            .setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");

                    PauseTransition closePause = new PauseTransition(Duration.seconds(1));
                    closePause.setOnFinished(ev -> {
                        ((Stage) registerButton.getScene().getWindow()).close();
                    });
                    closePause.play();
                } else {
                    showErrorAlert("Lỗi Server", "Không thể hoàn thành quy trình đăng tải sản phẩm.");
                    resetButtonState();
                }
            });

            uploadTask.setOnFailed(e -> {
                LOGGER.error("Task thất bại!");
                showErrorAlert("Lỗi kết nối", "Có lỗi xảy ra trong quá trình gửi dữ liệu.");
                LOGGER.error("Upload task error", uploadTask.getException());
                resetButtonState();
            });

            // --- BƯỚC 5: CHẠY BẰNG VIRTUAL THREAD EXECUTOR ---
            ClientContext.EXECUTOR.execute(uploadTask);

        } catch (ItemValidationException e) {
            showErrorAlert("Lỗi nhập liệu", e.getMessage());
            resetButtonState();
        } catch (NumberFormatException e) {
            showErrorAlert("Lỗi định dạng", "Giá phải là số!");
            resetButtonState();
        } catch (DateTimeParseException e) {
            showErrorAlert("Lỗi định dạng thời gian",
                    "Vui lòng nhập đúng định dạng thời gian: Giờ : phút");
            resetButtonState();
        }
    }

    private void updateRegisterButtonState() {
        // Kiểm tra: giá không trống VÀ ngày đã được chọn
        boolean isPriceEntered = priceField.getText() != null && !priceField.getText().trim().isEmpty();
        boolean isDateSelected = auctionEndPicker.getValue() != null;
        boolean isTimeEntered = endTimeField.getText() != null && !endTimeField.getText().trim().isEmpty();
        boolean isImageSelected = selectedFile != null;
        boolean canProceed = isPriceEntered && isDateSelected && isTimeEntered && isImageSelected;
        // Nếu cả 2 đều thỏa mãn thì enable nút, ngược lại thì disable
        registerButton.setDisable(!canProceed);

        // Cập nhật màu sắc
        if (!canProceed) {
            btnNext.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666;");
        } else {
            btnNext.setStyle("-fx-background-color: #00008b; -fx-cursor: hand;");
        }
    }

    // Thêm các phần của từng danh mục
    private LocalDateTime getAuctionStartTime() throws ItemValidationException.InvalidDurationException {
        LocalDate startDate = auctionStartPicker.getValue();
        String startTimeText = startTimeField.getText() == null ? "" : startTimeField.getText().trim();

        if (startDate == null && startTimeText.isEmpty()) {
            return LocalDateTime.now();
        }

        if (startDate == null || startTimeText.isEmpty()) {
            throw new ItemValidationException.InvalidDurationException(
                    "Vui lòng nhập đủ ngày và giờ bắt đầu, hoặc bỏ trống cả hai để bắt đầu ngay!");
        }

        LocalDateTime startDateTime = LocalDateTime.of(startDate, parseTime(startTimeText));
        if (startDateTime.isBefore(LocalDateTime.now())) {
            throw new ItemValidationException.InvalidDurationException(
                    "Thời gian bắt đầu không được ở quá khứ!");
        }

        return startDateTime;
    }

    private LocalDateTime getAuctionEndTime() {
        LocalDate endDate = auctionEndPicker.getValue();
        LocalTime endTime = parseTime(endTimeField.getText() == null ? "" : endTimeField.getText().trim());
        return LocalDateTime.of(endDate, endTime);
    }

    private LocalTime parseTime(String timeText) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(timeText, timeFormatter);
    }

    private void disableDatePickerTextInput(DatePicker datePicker) {
        TextField editor = datePicker.getEditor();
        editor.setEditable(false);

        editor.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.DELETE) {
                event.consume();
            }
        });
        editor.setContextMenu(new ContextMenu());
    }

    private TextField addTextField(String labelText, String promptText) {
        Label label = new Label(labelText);
        TextField textField = new TextField();
        textField.setPromptText(promptText);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 10px;");

        dynamicTextFields.add(textField);
        dynamicErrorLabels.add(errorLabel);

        textField.textProperty().addListener((obs, old, newVal) -> {
            if (newVal.trim().isEmpty()) {
                return;
            }

            if (INTEGER_FIELD.equals(textField.getUserData()) && !isInteger(newVal.trim())) {
                setFieldError(textField, errorLabel, "Thông tin này chỉ chứa số nguyên.");
            } else {
                hideFieldError(textField, errorLabel);
            }
        });
        // BỌC CẢ 3 VÀO VBOX ĐỂ TẠO CỤM RIÊNG
        VBox fieldGroup = new VBox(5, label, textField, errorLabel);
        // THÊM CẢ CỤM VÀO CONTAINER CHA
        dynamicCategoryContainer.getChildren().add(fieldGroup);

        return textField;
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showFieldError(TextInputControl control, Label errorLabel, String message) {
        setFieldError(control, errorLabel, message);
        control.textProperty().addListener((o, old, newVal) -> {
            if (!newVal.trim().isEmpty()) {
                hideFieldError(control, errorLabel);
            }
        });
    }

    private void setFieldError(TextInputControl control, Label errorLabel, String message) {
        control.setStyle("-fx-border-color: red;");
        errorLabel.setText(message);
    }

    private void hideFieldError(TextInputControl control, Label errorLabel) {
        control.setStyle("");
        errorLabel.setText("");
    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void setImageSelectedState(boolean selected) {
        if (removeImageBtn != null) {
            removeImageBtn.setVisible(selected);
            removeImageBtn.setManaged(selected);
        }
    }

    private void setUpVisibleFalse(Node child) {
        child.setVisible(false);
        child.setManaged(false);
    }

    private void setUpVisibleTrue(Node child) {
        child.setVisible(true);
        child.setManaged(true);
    }

    private void resetButtonState() {
        registerButton.setText("Đăng tải sản phẩm🚀"); // Tên ban đầu của nút
        registerButton.setDisable(false); // Mở khóa
        registerButton.setStyle("-fx-background-color: #00008b; -fx-text-fill: white; -fx-background-radius: 8");
    }
}
