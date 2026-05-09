package ddc.server.controller.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.gson.Gson;

import ddc.server.config.GsonConfig;
import ddc.server.controller.RequestMessage;
import ddc.server.exception.ItemValidationException;
import ddc.server.network.response.AddItemResponse;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.Response;
import ddc.server.pattern.factory.ItemRequest;

public class AddItemHandler implements ActionHandler {
    private final Gson gson = GsonConfig.newGson();

    @Override
    public Response handle(RequestMessage request) {
        try {
            if (request.getData() == null || request.getData().isBlank()) {
                return new BaseResponse().setStatus("INVALID_INPUT");
            }

            ItemRequest itemReq = gson.fromJson(request.getData(), ItemRequest.class);
            if (itemReq == null) {
                return new BaseResponse().setStatus("INVALID_INPUT");
            }

            // --- BƯỚC MỚI: XỬ LÝ LƯU ẢNH VẬT LÝ ---
            byte[] imageData = request.getImageData();
            System.out.println(">>> SERVER NHẬN ĐƯỢC ẢNH: " + (imageData != null ? imageData.length : "NULL"));

            if (imageData != null && imageData.length > 0) {
                saveImageToFile(itemReq.getImageUrl(), imageData);
            } else {
                System.out.println(">>> CẢNH BÁO: Không có dữ liệu ảnh để lưu!");
            }
            // --- LƯU DATABASE ---
            // Đảm bảo itemService.createAndSaveItem đã xử lý lưu itemReq.getImageUrl() vào cột tương ứng
            String id = itemService.createAndSaveItem(itemReq);
            
            if (id != null) {
                return new AddItemResponse()
                        .setId(id)
                        .setStatus("SUCCESS");
            }
            return new BaseResponse().setStatus("FAIL");
        } catch (ItemValidationException e) {
            return new BaseResponse().setStatus("INVALID_INPUT").setMessage(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse().setStatus("SERVER_ERROR");
        }
    }

    // Hàm phụ trợ ghi file
    private void saveImageToFile(String fileName, byte[] data) throws IOException {
        File uploadDir = new File("uploads");
        if (!uploadDir.exists()) uploadDir.mkdirs();

        File file = new File(uploadDir, fileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
        }
        System.out.println(">>> Đã lưu ảnh vào thư mục server: " + file.getAbsolutePath());
    }
}
