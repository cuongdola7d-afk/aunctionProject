package ddc.client.controller.loginregister;

import java.io.IOException;

import ddc.client.controller.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Login {
    @FXML
    private TextField usernameTextField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

@FXML
private void login(ActionEvent event) {
    try {
        Parent root = FXMLLoader.load(getClass().getResource("/ddc/client/views/home/Home.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
        System.out.println("Loi mo Home.fxml: " + e.getMessage());
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Error: " + e.getMessage());
    }
}

    @FXML
    private void switchToRegister(ActionEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/loginregister/Register.fxml");
    }
    
}
