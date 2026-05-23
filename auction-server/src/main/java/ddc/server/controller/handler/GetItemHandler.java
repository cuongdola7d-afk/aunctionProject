package ddc.server.controller.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddc.server.controller.RequestMessage;
import ddc.server.model.item.ItemGeneric;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.GetItemResponse;
import ddc.server.network.response.Response;

public class GetItemHandler implements ActionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetItemHandler.class);

    @Override
    public Response handle(RequestMessage request) {
        try {
            String itemId = gson.fromJson(request.getData(), String.class);
            if (isBlank(itemId)) {
                return new BaseResponse().setStatus("INVALID_INPUT").setMessage("Thieu ID san pham.");
            }
            ItemGeneric item = itemService.getItemDetails(itemId);

            return new GetItemResponse()
                    .setItemJson(gson.toJson(item))
                    .setStatus("SUCCESS");
        } catch (Exception e) {
            LOGGER.error("Loi lay san pham", e);
            return new BaseResponse().setStatus("SERVER_ERROR").setMessage("Loi server khi lay san pham.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
