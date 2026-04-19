package ddc.server.controller.handler;

import com.google.gson.Gson;

import ddc.server.config.GsonConfig;
import ddc.server.controller.RequestMessage;
import ddc.server.dao.ItemDAO;
import ddc.server.model.item.Item;
import ddc.server.service.ItemService;
import ddc.server.pattern.factory.ItemCreator.ItemRequest;

public class AddItemHandler implements ActionHandler {
    private final Gson gson = GsonConfig.newGson();
    // Thay vì gọi trực tiếp DAO, ta dùng Service
    private final ItemService itemService = new ItemService(); 

    @Override
    public String handle(RequestMessage request) {
        // 1. Chuyển dữ liệu String từ Request thành đối tượng ItemRequest (DTO)
        ItemRequest itemReq = gson.fromJson(request.getData(), ItemRequest.class);

        System.out.println("Đang xử lý tạo sản phẩm: " + itemReq.getName());

        // 2. Nhờ Service xử lý (Service sẽ gọi Factory và DAO)
        boolean isSuccess = itemService.createAndSaveItem(itemReq);

        // 3. Trả về kết quả cho ClientHandler để nó gửi về máy khách
        return isSuccess ? "\"SUCCESS\"" : "\"FAIL\"";
    }
}