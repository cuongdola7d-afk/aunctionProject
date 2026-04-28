package ddc.client.network;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import ddc.client.config.GsonConfig;
import ddc.client.model.Request;
import ddc.client.network.response.BaseResponse;

public class ClientToServer {
    private static final Gson gson = GsonConfig.newGson();
    private static final String HOST = readConfig("DDC_SERVER_HOST", "ddc.server.host", "localhost");
    private static final int PORT = readIntConfig("DDC_SERVER_PORT", "ddc.server.port", 8080);
    private static final int TIMEOUT_MS = 10_000;

    private ClientToServer() {}

    public static String sendRequest(String action, Object obj) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(HOST, PORT), TIMEOUT_MS);
            socket.setSoTimeout(TIMEOUT_MS);

            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

                Request request = new Request(action, obj);
                out.println(gson.toJson(request));

                String response = in.readLine();
                if (response == null || response.isBlank()) {
                    return errorJson("EMPTY_RESPONSE", "Server khong tra response.");
                }
                return response;
            }
        } catch (Exception e) {
            return errorJson("CONNECTION_ERROR", "Khong ket noi duoc server.");
        }
    }

    private static String errorJson(String status, String message) {
        return gson.toJson(new BaseResponse().setMessage(message).setStatus(status));
    }

    private static String readConfig(String envName, String propertyName, String defaultValue) {
        String value = System.getenv(envName);
        if (value != null && !value.isBlank()) {
            return value;
        }

        value = System.getProperty(propertyName);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static int readIntConfig(String envName, String propertyName, int defaultValue) {
        String value = readConfig(envName, propertyName, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
