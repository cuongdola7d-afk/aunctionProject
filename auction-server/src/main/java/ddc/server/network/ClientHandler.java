package ddc.server.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import ddc.server.config.GsonConfig;
import ddc.server.controller.UserController;
import ddc.server.model.user.User;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final UserController userController;
    private final Gson gson;
    
    public ClientHandler (Socket socket) {
        this.clientSocket = socket;
        this.userController = new UserController();
        this.gson = GsonConfig.newGson();
    }

    @Override
    public void run () {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String receivedJson = in.readLine();
            if (receivedJson != null) {
                System.out.println("Handling Request: " + receivedJson);

                User requestUser = gson.fromJson(receivedJson, User.class);

                String response;
                
                switch (requestUser.getAction()) {
                    case "REGISTER":
                        response = userController.handleRegister(requestUser);
                        break;
                    case "LOGIN":
                        response = userController.handleLogin(requestUser);
                        break;
                    case "ADD ITEM":
                        
                    default:
                        response = "UNDEFINED BEHAVIOR!!!";
                        break;
                }

                out.println(response);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
