package ddc.server.network;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
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
        try {
            clientSocket.setSoTimeout(SOCKET_TIMEOUT_MS);
            // Sử dụng DataInputStream xuyên suốt để đọc cả Text và Byte
            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // 1. Đọc dòng JSON đầu tiên
            String rawRequest = readLineCustom(dis); 
            if (rawRequest == null) return;

            RequestMessage request = gson.fromJson(rawRequest, RequestMessage.class);

            // 2. Kiểm tra và đọc ảnh
            if (request != null && "ADD_ITEM".equals(request.getAction())) {
                try {
                    // Đọc độ dài ảnh (writeInt bên Client tương ứng readInt ở đây)
                    int imageLength = dis.readInt(); 
                    if (imageLength > 0) {
                        byte[] imageData = new byte[imageLength];
                        dis.readFully(imageData); // Đảm bảo đọc đủ số byte
                        request.setImageData(imageData);
                        System.out.println(">>> SERVER DA NHAN ANH: " + imageLength + " bytes");
                    } else {
                        System.out.println(">>> SERVER NHAN: imageLength = 0");
                    }
                } catch (IOException e) {
                    System.out.println(">>> LOI DOC ANH: " + e.getMessage());
                }
            }

            // 3. Xử lý và phản hồi
            String responseJson = handleRawRequest(request, rawRequest);
            out.println(responseJson);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Loi RequestClientHandler", e);
        } finally {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Khong the dong socket", e);
            }
        }
    }

    // Sửa lại hàm này để nhận InputStream hoặc DataInputStream
    private String readLineCustom(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        int b;
        while ((b = in.read()) != -1) {
            if (b == '\n') break;
            if (b != '\r') sb.append((char) b);
        }
        return (sb.length() == 0 && b == -1) ? null : sb.toString();
    }


    // GIỮ NGUYÊN HÀM NÀY NHƯ YÊU CẦU
    private String handleRawRequest(RequestMessage request, String rawRequest) {
        try {
            if (rawRequest == null || rawRequest.isBlank()) {
                return fail("EMPTY_REQUEST", "Request rong.");
            }

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