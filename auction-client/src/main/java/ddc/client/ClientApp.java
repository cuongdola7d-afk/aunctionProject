package ddc.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClientApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
        getClass().getResource("/ddc/client/views/bidding/bidding-demo.fxml")
);

        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(
        getClass().getResource("/ddc/client/css/bidding/bidding-demo.css").toExternalForm());

        stage.setTitle("Bidding Demo");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}