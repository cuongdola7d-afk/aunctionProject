package ddc.client;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ClientApp extends Application{
    @Override
    public void start (Stage stage) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/loginregister/Login.fxml"));
        Parent root = loader.load();

        Image icon = new Image(getClass().getResourceAsStream("views/DDCAuction.png"));

        Scene scene = new Scene(root, 400, 500);

        stage.getIcons().add(icon);

        stage.setTitle("DDC Auction");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}