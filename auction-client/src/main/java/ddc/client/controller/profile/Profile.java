package ddc.client.controller.profile;

import java.io.IOException;

import ddc.client.controller.SceneSwitcher;
import ddc.client.network.UserSession;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Profile {
    private static final Logger LOGGER = LoggerFactory.getLogger(Profile.class);
    @FXML
    private Label nameLabel, usernameLabel;

    public void initialize() {
        UserSession session = UserSession.getInstance();

        // Đổ dữ liệu vào các ô TextField
        nameLabel.setText(session.getName());
        usernameLabel.setText("@" + session.getUsername());
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToSelling(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/selling/Selling.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToBidding(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/bidding/Bidding.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToHome(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/home/Home.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToPersonalInfo(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/profile/Personalinfo.fxml");
    }

    @FXML
    private void switchToNotify(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/notify/Notify.fxml");
    }

    @FXML
    private void switchToSecurity(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/profile/Security.fxml");
    }

    @FXML
    @SuppressWarnings("unused")
    private void switchToWallet(MouseEvent event) {
        Stage owner = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Label titleLabel = new Label("Ví của tôi");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #001f3f;");

        Label balanceTitleLabel = new Label("Số dư hiện tại");
        balanceTitleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666;");

        Label balanceLabel = new Label(formatBalance(0));
        balanceLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: darkblue;");

        Button depositButton = new Button("Nạp tiền");
        depositButton.setPrefWidth(120);
        depositButton.setStyle("-fx-background-color: darkblue; -fx-text-fill: white; -fx-background-radius: 8;");
        depositButton.setOnAction(actionEvent -> showWalletMessage("Chức năng nạp tiền đang được phát triển."));

        Button closeButton = new Button("Đóng");
        closeButton.setPrefWidth(90);

        HBox actions = new HBox(10, depositButton, closeButton);
        actions.setAlignment(Pos.CENTER);

        VBox root = new VBox(12, titleLabel, balanceTitleLabel, balanceLabel, actions);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #d9e2ef; -fx-border-radius: 12;");

        Stage walletStage = new Stage();
        walletStage.setTitle("Ví của tôi");
        walletStage.setResizable(false);
        walletStage.initModality(Modality.APPLICATION_MODAL);
        walletStage.initOwner(owner);
        closeButton.setOnAction(actionEvent -> walletStage.close());

        walletStage.setScene(new Scene(root, 320, 200));
        walletStage.setX(owner.getX() + (owner.getWidth() - 320) / 2);
        walletStage.setY(owner.getY() + (owner.getHeight() - 200) / 2);
        walletStage.show();
    }

    private String formatBalance(double balance) {
        return String.format("%,.0f VND", balance);
    }

    private void showWalletMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ví của tôi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    @SuppressWarnings("unused")
    private void showLogoutPopup(MouseEvent event) {
        try {
            // 1. Chỉ nạp FXML, TUYỆT ĐỐI không dùng SceneSwitcher ở đây
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ddc/client/views/profile/logout.fxml"));
            Parent root = loader.load();

            // 2. Tạo một cửa sổ MỚI (Stage mới)
            Stage popupStage = new Stage();
            popupStage.setTitle("Xác nhận đăng xuất");
            popupStage.setResizable(false);

            Image icon = new Image(getClass().getResourceAsStream("/ddc/client/views/DDCAuction.png"));
            popupStage.getIcons().add(icon);

            // 3. Khóa màn hình chính (Profile) ở phía sau
            popupStage.initModality(Modality.APPLICATION_MODAL);

            // 4. Chỉ định "chủ sở hữu" là cửa sổ Profile hiện tại
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            popupStage.initOwner(primaryStage);

            // 5. Tạo Scene mới
            Scene scene = new Scene(root, 400, 300);
            popupStage.setScene(scene);

            // 6. Hiển thị cửa sổ mới lên
            popupStage.centerOnScreen();
            popupStage.show();

        } catch (IOException e) {
            LOGGER.error("Loi hien thi popup logout", e);
        }
    }
}
