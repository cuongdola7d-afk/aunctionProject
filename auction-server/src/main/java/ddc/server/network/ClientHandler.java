package ddc.server.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import ddc.server.config.GsonConfig;
import ddc.server.controller.RequestMessage;
import ddc.server.controller.handler.ActionHandler;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Gson gson;
    
    public ClientHandler (Socket socket) {
        this.clientSocket = socket;
        this.gson = GsonConfig.newGson();
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    public void run () {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            RequestMessage request = gson.fromJson(in.readLine(), RequestMessage.class);

            ActionHandler handler = RequestRouter.getHandler(request.getAction());

            String response;

            if (handler != null) {
                response = handler.handle(request);
            } else {
                response = "UNDEFINED BEHAVIOR!!!";
            }

            out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
