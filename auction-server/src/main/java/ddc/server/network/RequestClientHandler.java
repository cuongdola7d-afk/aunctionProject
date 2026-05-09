package ddc.server.network;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import ddc.server.config.GsonConfig;
import ddc.server.controller.RequestMessage;
import ddc.server.controller.handler.ActionHandler;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.Response;

public class RequestClientHandler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(RequestClientHandler.class.getName());
    private static final int SOCKET_TIMEOUT_MS = 10_000;
    private static final int MAX_REQUEST_LENGTH = 16_384;

    private final Socket clientSocket;
    private final Gson gson;
    
    public RequestClientHandler(Socket socket) {
        this.clientSocket = socket;
        this.gson = GsonConfig.newGson();
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            
            clientSocket.setSoTimeout(SOCKET_TIMEOUT_MS);

            // TẤT CẢ request (Login hay Ảnh) đều bắt đầu bằng 1 dòng JSON
            String rawRequest = in.readLine(); 
            if (rawRequest == null) return;

            RequestMessage request = gson.fromJson(rawRequest, RequestMessage.class);

            // Chỉ khi là ADD_ITEM mới đọc tiếp phần Byte ảnh
            if (request != null && "ADD_ITEM".equals(request.getAction())) {
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                int imageLength = dis.readInt(); // Đọc độ dài ảnh
                if (imageLength > 0) {
                    byte[] imageData = new byte[imageLength];
                    dis.readFully(imageData); // Đọc đủ số byte ảnh
                    request.setImageData(imageData);
                }
            }

            // Xử lý và trả về
            String responseJson = handleRawRequest(request, rawRequest);
            out.println(responseJson); // Trả về 1 dòng JSON cho Client

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Loi request: " + e.getMessage());
        }
    }

    // Giữ nguyên tên hàm handleRawRequest như bạn yêu cầu
    private String handleRawRequest(RequestMessage request, String rawRequest) {
        try {
            if (rawRequest == null || rawRequest.isBlank()) {
                return fail("EMPTY_REQUEST", "Request rong.");
            }

            // Kiểm tra độ dài chuỗi JSON (MAX_REQUEST_LENGTH)
            if (rawRequest.length() > MAX_REQUEST_LENGTH) {
                return fail("REQUEST_TOO_LARGE", "Request vuot gioi han.");
            }

            if (request == null || isBlank(request.getAction())) {
                return fail("INVALID_REQUEST", "Thieu action.");
            }

            ActionHandler handler = RequestRouter.getHandler(request.getAction());
            if (handler == null) {
                return fail("UNKNOWN_ACTION", "Action khong duoc ho tro.");
            }

            // Thực hiện xử lý (Handler sẽ lấy imageData từ trong request ra)
            return gson.toJson(handler.handle(request));

        } catch (JsonSyntaxException e) {
            LOGGER.log(Level.WARNING, "Request JSON khong hop le.", e);
            return fail("INVALID_JSON", "JSON khong hop le.");
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Handler xu ly loi.", e);
            return fail("SERVER_ERROR", "Loi server.");
        }
    }
    
    private String fail(String status, String message) {
        Response<?> response = new BaseResponse().setStatus(status).setMessage(message);
        return gson.toJson(response);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
