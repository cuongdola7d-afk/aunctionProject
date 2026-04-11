package ddc.client;

import java.io.IOException;

import ddc.client.controller.bidding.Bidding;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Client extends Application{
    @Override
    public void start (Stage stage) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ddc/client/views/loginregister/login.fxml"));

        Parent root = loader.load();

        /*Bidding controller = loader.getController();
        String currentUserBidderId = "BIDDER-BOB";
        controller.setupBidderContext(currentUserBidderId);*/

        Image icon = new Image(getClass().getResourceAsStream("views/DDCAuction.png"));

        Scene scene = new Scene(root, 400, 500);

        stage.getIcons().add(icon);

        stage.setTitle("DDC Auction");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}