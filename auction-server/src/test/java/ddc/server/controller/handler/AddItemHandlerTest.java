package ddc.server.controller.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import org.junit.jupiter.api.Test;

import ddc.server.controller.RequestMessage;
import ddc.server.controller.service.ItemService;
import ddc.server.network.response.Response;
import ddc.server.network.response.AddItemResponse;

class AddItemHandlerTest {
    // Mock ItemService để cô lập hoàn toàn database và Cloudinary
    private final ItemService itemService = mock(ItemService.class);
    private final AddItemHandler handler = new AddItemHandler(itemService);

    @Test
    void testAddItemSuccess() throws Exception {
        // 1. Tạo data giả lập là JSON String
        String artJson = "{" +
                                "\"category\":\"ART\"," +
                                "\"itemName\":\"Tranh Đông Hồ\"," +
                                "\"description\":\"Tranh Đông Hồ nghệ thuật dân gian Việt Nam\"," +
                                "\"sellerName\":\"admin\"," +
                                "\"author\":\"Nghệ nhân\"," +
                                "\"yearCreated\":2024" + 
                                "}";
        byte[] fakeImage = {1, 2, 3};

        // Giả lập hành vi của Service trả về ID thành công khi lưu
        when(itemService.processUploadAndSave(anyString(), any())).thenReturn("I00001");

        // 2. Đưa vào RequestMessage
        RequestMessage request = new RequestMessage("ADD_ITEM", artJson);
        request.setImageData(fakeImage);

        // 3. Gọi trực tiếp hàm handle của class bạn vừa gửi
        Response response = handler.handle(request);

        // 4. Kiểm tra kết quả
        assertEquals("SUCCESS", response.getStatus());
        assertTrue(response instanceof AddItemResponse);
        assertEquals("I00001", ((AddItemResponse) response).getId());
    }

    @Test
    void testAddItemError() throws Exception {
        // Giả lập hành vi của Service ném Exception khi gặp JSON lỗi
        when(itemService.processUploadAndSave(anyString(), any())).thenThrow(new RuntimeException("JSON_BI_LOI"));

        // Test trường hợp JSON lỗi để xem catch (Exception e) có hoạt động không
        RequestMessage request = new RequestMessage("ADD_ITEM", "JSON_BI_LOI");

        Response response = handler.handle(request);

        assertEquals("ERROR", response.getStatus());
    }
}