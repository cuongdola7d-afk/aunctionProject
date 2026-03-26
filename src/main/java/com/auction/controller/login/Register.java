package com.auction.controller.login;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Register implements Initializable{

    @FXML
    private Label roleLabel;
    @FXML
    private ChoiceBox<String> roleChoiceBox;

    private String[] choice = {"As A Seller", "As A Bidder"};
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
    
    public void initialize(URL arg0, ResourceBundle arg1) {
        roleChoiceBox.getItems().addAll(choice);
        roleChoiceBox.setOnAction(this::getChoice);
    }

    public void getChoice(ActionEvent event) {
        String myChoice = roleChoiceBox.getValue();
        if (myChoice.equals("As A Seller")) {
            roleLabel.setText("Business Name");
        }
        else if (myChoice.equals("As A Bidder")) {
            roleLabel.setText("Username");
        }
    }
}
