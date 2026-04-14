package ddc.client.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import ddc.client.model.User;

public class ClientToServer {
    private static final Gson gson = new Gson();

    public static String toServer (User requestUser) {

        String jsonString = gson.toJson(requestUser);
        System.out.println("Sending...");

        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            out.println(jsonString);

            String response = in.readLine();
            System.out.println("Response: " + response);

            return response;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Can't connect to Server!";
        }
    }
}
