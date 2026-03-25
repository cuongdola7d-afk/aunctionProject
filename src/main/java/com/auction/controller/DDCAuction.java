package com.auction.controller;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class DDCAuction extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/auction/login/Login.fxml"));
            Image icon = new Image(getClass().getResourceAsStream("/com/auction/login/DDCAuction.png"));
            stage.getIcons().add(icon);
            Scene scene = new Scene(root, 400, 400);
            String css = this.getClass().getResource("/com/auction/login/Login.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setTitle("DDCAuction");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

