package ddc.server.controller.handler;

import ddc.server.controller.RequestMessage;
import ddc.server.controller.service.ItemService;

import ddc.server.network.response.AddItemResponse;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.Response;
import ddc.server.pattern.factory.ItemRequest;

public class AddItemHandler implements ActionHandler {
    // Thay vì gọi trực tiếp DAO, ta dùng Service
    private final ItemService itemService = new ItemService(); 

    @Override
    public Response handle(RequestMessage request) {
        try {
            System.out.println(">>> Server đang nhận ADD_ITEM...");
            
            // 1. Kiểm tra xem dữ liệu thô có vào đến đây không
            if (request.getData() == null) return new BaseResponse().setStatus("FAIL");
            
            ItemRequest itemReq = gson.fromJson(request.getData(), ItemRequest.class);
            
            // 2. Log thử một biến để xem parse thành công không
            System.out.println(">>> Parse thành công SP: " + itemReq.getItemName());

            // 3. Thực hiện lưu
            String id = itemService.createAndSaveItem(itemReq);
            
            if (id != null) {
                return new AddItemResponse().setStatus("SUCCESS")
                                            .setId(id);
            } else {
                return new BaseResponse().setStatus("FAIL");
            }

        } catch (Throwable t) { 
            // Dùng Throwable để bắt TẤT CẢ mọi loại lỗi kể cả Error nghiêm trọng
            System.err.println("!!! SERVER CRASHED TẠI HANDLER !!!");
            t.printStackTrace(); 
            return new BaseResponse().setStatus("FAIL");
        }
    }
}