package ddc.server.controller.handler;

import ddc.server.controller.RequestMessage;
import ddc.server.exception.ItemValidationException;
import ddc.server.network.response.AddItemResponse;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.Response;
import ddc.server.pattern.factory.ItemRequest;

public class AddItemHandler implements ActionHandler {

    @Override
    @SuppressWarnings("UseSpecificCatch")
    public Response handle(RequestMessage request) {
        try {
            if (request.getData() == null || request.getData().isBlank()) {
                return new BaseResponse().setStatus("INVALID_INPUT");
            }

            ItemRequest itemReq = gson.fromJson(request.getData(), ItemRequest.class);
            if (itemReq == null) {
                return new BaseResponse().setStatus("INVALID_INPUT");
            }

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
            return new BaseResponse().setStatus("SERVER_ERROR");
        }
    }
}
