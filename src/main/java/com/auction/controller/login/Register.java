package com.auction.controller.login;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Register {
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void redirect(ActionEvent event) throws IOException {
        try {
            root = FXMLLoader.load(getClass().getResource("/com/auction/login/Login.fxml"));
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            Image icon = new Image(getClass().getResourceAsStream("/com/auction/login/DDCAuction.png"));
            scene = new Scene(root, 400, 400);
            stage.getIcons().add(icon);
            String css = this.getClass().getResource("/com/auction/login/login.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setTitle("DDCAuction");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }
}
