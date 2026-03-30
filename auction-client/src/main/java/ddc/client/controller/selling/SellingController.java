package ddc.client.controller.selling;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SellingController {

    // Hàm này phải trùng tên với "On Action" bạn đặt trong Scene Builder
    @FXML
    private void handleOpenUploadDialog() {
        try {
            // 1. Load file FXML của cái Dialog nhập liệu
            // Chú ý: Đường dẫn phải bắt đầu từ thư mục resources
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ddc/client/views/selling/UploadItemDescrip.fxml"));
            Parent root = loader.load();

            // 2. Tạo Stage mới (Cửa sổ mới)
            Stage stage = new Stage();
            stage.setTitle("Tạo mục đấu giá mới");
            stage.setResizable(false);
            stage.centerOnScreen();
            Image icon = new Image(getClass().getResourceAsStream("/ddc/client/views/DDCAuction.png"));
            stage.getIcons().add(icon);
            
            // 3. Khóa màn hình chính cho đến khi đóng cửa sổ này (Modal)
            stage.initModality(Modality.APPLICATION_MODAL);
            
            // 4. Thiết lập Scene và hiển thị
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi: Không tìm thấy file FXML. Hãy kiểm tra lại đường dẫn!");
        }
    }
}