package ddc.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
;

public class DDCAuction extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/selling/Selling.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 600);
        
        Image icon = new Image(getClass().getResourceAsStream("views/DDCAuction.png"));

        stage.setTitle("DDCAuction");
        stage.getIcons().add(icon);
        stage.setFullScreen(true);
        stage.setResizable(false);

        stage.setScene(scene);
        stage.show();
    }
}
