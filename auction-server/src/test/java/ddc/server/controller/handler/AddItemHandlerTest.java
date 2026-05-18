package ddc.server.controller.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import ddc.server.controller.RequestMessage;
import ddc.server.network.response.Response;

class AddItemHandlerTest {
    // Khởi tạo handler thật của server
    private final AddItemHandler handler = new AddItemHandler();

    // @Test
    // void testAddItemSuccess() {
    //     // 1. Tạo data giả lập là JSON String
    //     String artJson = "{" +
    //                             "\"category\":\"ART\"," +
    //                             "\"itemName\":\"Tranh Đông Hồ\"," +
    //                             "\"sellerName\":\"admin\"," +
    //                             "\"author\":\"Nghệ nhân\"," +
    //                             "\"yearCreated\":2024" + 
    //                             "}";
    //     byte[] fakeImage = {1, 2, 3};

    //     // 2. Đưa vào RequestMessage
    //     RequestMessage request = new RequestMessage("ADD_ITEM",artJson);
    //     request.setImageData(fakeImage);

    //     // 3. Gọi trực tiếp hàm handle của class bạn vừa gửi
    //     Response response = handler.handle(request);

    //     // 4. Kiểm tra kết quả
    //     assertEquals("SUCCESS", response.getStatus());
    //     assertTrue(response instanceof AddItemResponse);
    //     assertNotNull(((AddItemResponse) response).getId());
    // }

    @Test
    void testAddItemError() {
        // Test trường hợp JSON lỗi để xem catch (Exception e) có hoạt động không
        RequestMessage request = new RequestMessage("ADD_ITEM","JSON_BI_LOI");

        Response response = handler.handle(request);

        assertEquals("ERROR", response.getStatus());
    }
}