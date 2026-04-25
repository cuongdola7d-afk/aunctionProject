package ddc.server.controller.handler;

import com.google.gson.Gson;

import ddc.server.config.GsonConfig;
import ddc.server.controller.RequestMessage;
import ddc.server.controller.service.ItemService;
import ddc.server.pattern.factory.itemcreating.ItemRequest;

public class AddItemHandler implements ActionHandler {
    private final Gson gson = GsonConfig.newGson();
    // Thay vì gọi trực tiếp DAO, ta dùng Service
    private final ItemService itemService = new ItemService(); 

    @Override
    public String handle(RequestMessage request) {
        try {
            System.out.println(">>> Server đang nhận ADD_ITEM...");
            
            // 1. Kiểm tra xem dữ liệu thô có vào đến đây không
            if (request.getData() == null) return "\"FAIL_DATA_EMPTY\"";
            
            ItemRequest itemReq = gson.fromJson(request.getData(), ItemRequest.class);
            
            // 2. Log thử một biến để xem parse thành công không
            System.out.println(">>> Parse thành công SP: " + itemReq.getItemName());

            // 3. Thực hiện lưu
            boolean isSuccess = itemService.createAndSaveItem(itemReq);
            
            return isSuccess ? "\"SUCCESS\"" : "\"FAIL\"";

        } catch (Throwable t) { 
            // Dùng Throwable để bắt TẤT CẢ mọi loại lỗi kể cả Error nghiêm trọng
            System.err.println("!!! SERVER CRASHED TẠI HANDLER !!!");
            t.printStackTrace(); 
            return "\"SERVER_CRASH: " + t.getMessage() + "\"";
        }
    }
}