package ddc.server.controller.handler;

import com.google.gson.Gson;

import ddc.server.config.GsonConfig;
import ddc.server.controller.RequestMessage;
import ddc.server.model.item.ItemGeneric;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.GetItemResponse;
import ddc.server.network.response.Response;

public class GetItemHandler implements ActionHandler {
    private final Gson gson = GsonConfig.newGson();

    @Override
    public Response handle(RequestMessage request) {
        try {
            String itemId = request.getData();
            if (isBlank(itemId)) {
                return new BaseResponse().setStatus("INVALID_INPUT").setMessage("Thieu ID san pham.");
            }

            ItemGeneric item = itemService.getItemDetails(itemId.trim());
            if (item == null) {
                return new BaseResponse().setStatus("NOT_FOUND").setMessage("Khong tim thay san pham.");
            }

            return new GetItemResponse()
                    .setItemJson(gson.toJson(item))
                    .setStatus("SUCCESS");
        } catch (Exception e) {
            return new BaseResponse().setStatus("SERVER_ERROR").setMessage("Loi server khi lay san pham.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
