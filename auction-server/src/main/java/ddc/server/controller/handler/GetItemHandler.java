package ddc.server.controller.handler;

import ddc.server.controller.RequestMessage;
import ddc.server.controller.service.ItemService;
import ddc.server.model.item.ItemGeneric;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.GetItemResponse;
import ddc.server.network.response.Response;

public class GetItemHandler implements ActionHandler{
    private final ItemService itemService = new ItemService();

    @Override
    public Response handle(RequestMessage request) {
        try {
            System.out.println("Getting item...");

            if (request.getData() == null) return new BaseResponse().setStatus("FAIL");

            ItemGeneric item = itemService.getItemDetails(gson.fromJson(request.getData(), String.class));

            if (item != null) {
                System.out.println("DONE!");
                return new GetItemResponse().setStatus("SUCCESS")
                                            .setItemJson(gson.toJson(item));
            } else {
                return new BaseResponse().setStatus("FAIL");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse().setStatus("FAIL");
        }
    }
}
