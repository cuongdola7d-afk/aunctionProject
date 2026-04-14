package ddc.server.network.debug;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ManualSocketClient {
    public static void main(String[] args) {
        try (
                Socket socket = new Socket("localhost", 5555);
                BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))
        ) {
            Thread readerThread = new Thread(() -> {
                try {
                    String line;
                    while ((line = serverReader.readLine()) != null) {
                        System.out.println("[SERVER] " + line);
                    }
                } catch (Exception e) {
                    System.out.println("Disconnected from server: " + e.getMessage());
                }
            });

            readerThread.setDaemon(true);
            readerThread.start();

            System.out.println("Connected to server.");
            System.out.println("Type JSON messages below:");

            String input;
            while ((input = consoleReader.readLine()) != null) {
                writer.println(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}