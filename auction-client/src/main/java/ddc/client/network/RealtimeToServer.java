package ddc.client.network;

import java.io.BufferedReader;
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

public class RealtimeToServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RealtimeToServer.class);
    private static final Gson gson = GsonConfig.newGson();
    private static final int TIMEOUT_MS = 10_000;

    private RealtimeToServer() {
    }

    public static String sendRequest(Request request) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ClientContext.SERVER_HOST, ClientContext.REALTIME_PORT), TIMEOUT_MS);
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
            return errorJson("CONNECTION_ERROR", "Realtime khong ket noi duoc server.");
        }
    }

    private static String errorJson(String status, String message) {
        return gson.toJson(new BaseResponse().setStatus(status).setMessage(message));
    }
}
