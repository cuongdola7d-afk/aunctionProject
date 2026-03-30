package ddc.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ddc/client/views/bidding/bidding.fxml")
        );

        Scene scene = new Scene(loader.load(), 1180, 760);
        scene.getStylesheets().add(
                getClass().getResource("/ddc/client/css/bidding/bidding.css").toExternalForm()
        );

        stage.setTitle("DDC Auction - Bidding");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}