package ddc.client.controller.loginregister;

import ddc.client.controller.SceneSwitcher;
import ddc.client.model.UserDTO;
import ddc.client.network.response.UserResponse;
import ddc.client.network.ClientToServer;
import ddc.client.network.UserSession;
import com.google.gson.Gson;
import ddc.client.config.GsonConfig;
import javafx.application.Platform;
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
    private final Gson gson = new Gson();

    @FXML
    private void login(ActionEvent event) {
        if (usernameTextField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            errorLabel.setText("Vui lòng nhập thông tin vào chỗ trống.");
        }
        else {
            errorLabel.setText("Đang đăng nhập...");
            String username = usernameTextField.getText();
            String password = passwordField.getText();

            UserDTO user = new UserDTO()
                            .setUsername(username)
                            .setPassword(password);

            new Thread(() -> {
                String response = ClientToServer.sendRequest("LOGIN", user);
                UserResponse userRes = gson.fromJson(response, UserResponse.class);
                

                if ("SUCCESS".equals(userRes.getStatus())) {
                    UserDTO User = userRes.getData(); // Lấy "cục" data đã được giải mã
                    // Đổ vào UserSession như cũ
                        UserSession.getInstance()
                                    .setId(User.getId())
                                    .setName(User.getName())
                                    .setUsername(User.getUsername())
                                    .setEmail(User.getEmail());
                    
                    Platform.runLater(() -> errorLabel.setText("Đăng nhập thành công!"));
                
                try {
                    Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("IO Error!" + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Error!" + e.getMessage());
                    }

                Platform.runLater(() -> {
                    try {
                        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
                        Parent root = FXMLLoader.load(getClass().getResource("/ddc/client/views/home/Home.fxml"));
                        Stage stage = new Stage();
                        Image icon = new Image(getClass().getResourceAsStream("/ddc/client/views/DDCAuction.png"));

                        stage.setTitle("DDC Auction");
                        stage.getIcons().add(icon);
                        stage.setResizable(true);
                        stage.centerOnScreen();
                        stage.setScene(new Scene(root, 800, 600));
                        stage.show();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        e.printStackTrace();
                    }
                });
            } else {
                Platform.runLater(() -> {
                    if (response.contains("PASSWORD LESS THAN 8")) {
                        errorLabel.setText("Mật khẩu phải có từ 8 ký tự trở lên!");
                    } else if (response.contains("UNAVAILABLE")) {
                        errorLabel.setText("Tài khoản không tồn tại.");
                    } else if (response.contains("WRONG PASSWORD")) {
                        errorLabel.setText("Mật khẩu đã nhập không đúng!");
                    }
                });
            }
            }).start();
        }
    }

    @FXML
    private void switchToRegister(ActionEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/loginregister/Register.fxml");
    }
}
