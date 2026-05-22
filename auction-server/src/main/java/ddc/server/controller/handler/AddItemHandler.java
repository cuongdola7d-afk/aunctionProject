package ddc.server.controller.handler;


import ddc.server.controller.RequestMessage;
import ddc.server.controller.service.ItemService;
import ddc.server.network.response.AddItemResponse;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.Response;


public class AddItemHandler implements ActionHandler {
    private final ItemService itemService;

    // Constructor mac dinh dung cho production
    public AddItemHandler() {
        this.itemService = new ItemService();
    }

    // Constructor dung cho Unit Test de inject mock Service
    public AddItemHandler(ItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    public Response handle(RequestMessage request) {
        try {
            // Gọi hàm xử lý tổng hợp: 
            // Truyền vào chuỗi JSON (request.getData()) và mảng byte (request.getImageData())
            String id = itemService.processUploadAndSave(request.getData(), request.getImageData());
            
            if (id != null) {
                return new AddItemResponse().setStatus("SUCCESS").setId(id);
            }
        } catch (Exception e) {
            return new BaseResponse().setStatus("ERROR").setMessage(e.getMessage());
        }
        return new BaseResponse().setStatus("ERROR").setMessage("Thất bại");
    }
}
