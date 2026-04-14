package ddc.server.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import ddc.server.controller.UserController;
import ddc.server.model.user.User;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final UserController userController;
    private final Gson gson;
    
    public ClientHandler (Socket socket) {
        this.clientSocket = socket;
        this.userController = new UserController();

        JsonDeserializer<User> userDeserializer = new JsonDeserializer<User>() {
            @Override
            public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject jsonObject = json.getAsJsonObject();
                return new User.Builder()
                        .action(jsonObject.has("action") ? jsonObject.get("action").getAsString() : null)
                        .username(jsonObject.has("username") ? jsonObject.get("username").getAsString() : null)
                        .name(jsonObject.has("name") ? jsonObject.get("name").getAsString() : jsonObject.get("username").getAsString())
                        .email(jsonObject.has("email") ? jsonObject.get("email").getAsString() : null)
                        .password(jsonObject.has("password") ? jsonObject.get("password").getAsString() : null)
                        .build();
            }
        };
        this.gson = new GsonBuilder().registerTypeAdapter(User.class, userDeserializer).create();
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
                    default:
                        response = "Error!";
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
