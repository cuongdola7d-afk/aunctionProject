package ddc.client.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import ddc.client.model.Request;

public class ClientToServer {
    private static final Gson gson = new Gson();

    public static String sendRequest (String action, Object obj) {
        try (Socket socket = new Socket("localhost", 8080);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            Request request = new Request(action, obj);
            
            String jsonString = gson.toJson(request);
            System.out.println("Sending2: " + jsonString);
            out.println(jsonString);

            String response = in.readLine();
            System.out.println("Response2: " + response);

            return response;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return "Can't connect to Server!";
        }
    }
}
