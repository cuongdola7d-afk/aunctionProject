package com.auction.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.paint.Color;;

public class DDCAuction extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/auction/fxml/Selling.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 800, 600);
        
        Image icon = new Image(getClass().getResourceAsStream("/com/auction/Image/icon.png"));

        stage.setTitle("DDCAuction");
        stage.getIcons().add(icon);
        stage.setFullScreen(true);
        stage.setResizable(false);

        stage.setScene(scene);
        stage.show();
    }
}
