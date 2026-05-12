package ddc.client.network;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ddc.client.config.ClientContext;
import ddc.client.config.GsonConfig;
import ddc.client.model.Request;
import ddc.client.network.response.BaseResponse;

public class RequestToServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestToServer.class);
    private static final Gson gson = GsonConfig.newGson();
    private static final int TIMEOUT_MS = 10_000;

    private RequestToServer() {
    }

    public static String sendRequest(Request request) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ClientContext.SERVER_HOST, ClientContext.REQUEST_PORT), TIMEOUT_MS);
            socket.setSoTimeout(TIMEOUT_MS);

            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                LOGGER.info("Sending: {} , {}", request.getAction(), request.getData());
                out.println(gson.toJson(request));
                String response = in.readLine();
                if (response == null || response.isBlank()) {
                    return errorJson("EMPTY_RESPONSE", "Server khong tra response.");
                }
                LOGGER.debug("Response: {}", response);
                return response;
            }
        } catch (Exception e) {
            return errorJson("CONNECTION_ERROR", "Request khong ket noi duoc server.");
        }
    }

    private static String errorJson(String status, String message) {
        return gson.toJson(new BaseResponse().setStatus(status).setMessage(message));
    }

    public static String sendRequestWithImage(Request request, byte[] imageData) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ClientContext.SERVER_HOST, ClientContext.REQUEST_PORT), TIMEOUT_MS);

            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            InputStream is = socket.getInputStream();

            // Bước 1: Gửi JSON + dấu xuống dòng (Dùng writeBytes để giống println)
            String json = gson.toJson(request);
            dos.writeBytes(json + "\n");
            dos.flush();

            // Bước 2: Gửi dữ liệu ảnh
            if (imageData != null && imageData.length > 0) {
                dos.writeInt(imageData.length); // Gửi 4-byte int
                dos.write(imageData); // Gửi mảng byte
            } else {
                dos.writeInt(0);
            }
            dos.flush();

            // Bước 3: Đọc phản hồi (Đọc từng byte cho đến \n)
            StringBuilder sb = new StringBuilder();
            int b;
            while ((b = is.read()) != -1) {
                if (b == '\n')
                    break;
                sb.append((char) b);
            }
            return sb.toString().trim();

        } catch (Exception e) {
            return errorJson("ERROR", "Loi gui anh: " + e.getMessage());
        }
    }
}
