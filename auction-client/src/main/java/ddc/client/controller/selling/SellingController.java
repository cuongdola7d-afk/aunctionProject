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
    @FXML
    private void handleOpenUploadDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ddc/client/views/selling/UploadItemDescrip.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Tạo mục đấu giá mới");
            stage.setResizable(false);
            stage.centerOnScreen();
            Image icon = new Image(getClass().getResourceAsStream("/ddc/client/views/DDCAuction.png"));
            stage.getIcons().add(icon);
            
            stage.initModality(Modality.APPLICATION_MODAL);
            
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Lỗi: Không tìm thấy file FXML. Hãy kiểm tra lại đường dẫn!");
        }
    
    }
}