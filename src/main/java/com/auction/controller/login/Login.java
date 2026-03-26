package com.auction.controller.login;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Login {
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label errorLabel;

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void login(ActionEvent event) throws IOException {
        try {
            if (passwordField.getText().equals("") && emailTextField.getText().equals("")) {
                errorLabel.setText("Enter your email and password please.");
            }
            else if (passwordField.getText().equals("")) {
                errorLabel.setText("Enter your password please.");
            }
            else if (emailTextField.getText().equals("")) {
                errorLabel.setText("Enter your email please.");
            }
            else {
                System.out.println(emailTextField.getText());
                System.out.println(passwordField.getText());
                System.out.println("Good");
                System.out.println("Nice");
            }
        } catch (Exception e) {
            errorLabel.setText("Error!");
        }
        
    }

    public void redirect(ActionEvent event) throws IOException {
        try {
            root = FXMLLoader.load(getClass().getResource("/com/auction/login/Register.fxml"));
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            Image icon = new Image(getClass().getResourceAsStream("/com/auction/login/DDCAuction.png"));
            scene = new Scene(root, 400, 400);
            stage.getIcons().add(icon);
            String css = this.getClass().getResource("/com/auction/login/Register.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setTitle("DDCAuction");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
        }
    }
}

