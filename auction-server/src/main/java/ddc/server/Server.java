package ddc.server;

import java.net.ServerSocket;
import java.net.Socket;

import ddc.server.network.ClientHandler;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            System.out.println("Server Waiting for Client.");

            while (true) { 
                Socket clientSocket = serverSocket.accept();
                System.out.println("New Client: " + clientSocket.getInetAddress());

                ClientHandler handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}