package com.auction.controller.login;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
            }
        } catch (Exception e) {
            errorLabel.setText("Error!");
        }
        
    }
}

