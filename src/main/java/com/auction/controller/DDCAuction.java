package com.auction.controller;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.paint.Color;;

public class DDCAuction extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception{
        Group root = new Group();
        Scene scene = new Scene(root, Color.BLUEVIOLET);
        
        Image icon = new Image(getClass().getResourceAsStream("/com/auction/Image/icon.png"));

        stage.setTitle("DDCAuction");
        stage.getIcons().add(icon);

        stage.setScene(scene);
        stage.show();
    }
}
