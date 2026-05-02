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
            System.out.println("Connected to server.");
            System.out.println("Type JSON messages below:");
            // Dùng Virtual Thread để lắng nghe phản hồi từ Server
            Thread.ofVirtual().name("debug-reader").start(() -> {
                try {
                    String line;
                    while ((line = serverReader.readLine()) != null) {
                        System.out.println("[SERVER PHAN HOI] " + line);
                        System.out.print("> "); // Dấu nhắc nhắc người dùng nhập tiếp
                    }
                } catch (Exception e) {
                    System.err.println("\nDisconnected from server: " + e.getMessage());
                }
            });

            String input;
            while ((input = consoleReader.readLine()) != null) {
                writer.println(input);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}