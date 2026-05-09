package ddc.client.network;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.google.gson.Gson;

import ddc.client.config.GsonConfig;
import ddc.client.model.Request;
import ddc.client.network.response.BaseResponse;

public class RequestToServer {
    private static final Gson gson = GsonConfig.newGson();
    private static final String HOST = readConfig("DDC_SERVER_HOST", "ddc.server.host", "localhost");
    private static final int PORT = readIntConfig("DDC_SERVER_PORT", "ddc.server.port", 8080);
    private static final int TIMEOUT_MS = 10_000;

    private RequestToServer() {}

    public static String sendRequest(Request request) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(HOST, PORT), TIMEOUT_MS);
            socket.setSoTimeout(TIMEOUT_MS);

            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                System.out.println("Sending: " + request.getAction() + " , " + request.getData());
                out.println(gson.toJson(request));

                String response = in.readLine();
                if (response == null || response.isBlank()) {
                    return errorJson("EMPTY_RESPONSE", "Server khong tra response.");
                }
                System.out.println("Response: " + response);
                return response;
            }
        } catch (Exception e) {
            return errorJson("CONNECTION_ERROR", "Request khong ket noi duoc server.");
        }
    }

    private static String errorJson(String status, String message) {
        return gson.toJson(new BaseResponse().setStatus(status).setMessage(message));
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

    public static String sendRequestWithImage(Request request, byte[] imageData) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(HOST, PORT), TIMEOUT_MS);
            
            // Dùng DataOutputStream để ghi cả text và byte trên cùng 1 luồng
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Bước 1: Gửi JSON + dấu xuống dòng (\n) - RẤT QUAN TRỌNG
            dos.writeBytes(gson.toJson(request) + "\n");
            dos.flush(); 

            // Bước 2: Gửi dữ liệu ảnh
            if (imageData != null && imageData.length > 0) {
                dos.writeInt(imageData.length);
                dos.write(imageData);
            } else {
                dos.writeInt(0);
            }
            dos.flush();

            // Bước 3: Đọc phản hồi
            return in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return errorJson("ERROR", "Lỗi gửi ảnh");
        }
    }
}
