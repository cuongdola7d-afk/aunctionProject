package ddc.server.controller.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddc.server.controller.RequestMessage;
import ddc.server.model.transaction.Bid;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.GetAllUserBidResponse;
import ddc.server.network.response.Response;

public class GetAllUserBidHandler implements ActionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetAllUserBidHandler.class);

    @Override
    public Response<?> handle (RequestMessage request) {
        try {
            System.out.println(gson.fromJson(request.getData(), String.class));
            List<Bid> bids = bidService.getAll(gson.fromJson(request.getData(), String.class));

            if (bids == null) {
                return new BaseResponse().setStatus("FAIL");
            }

            return new GetAllUserBidResponse().setStatus("SUCCESS")
                                              .setData(bids);
        } catch (Exception e) {
            LOGGER.error("Loi lay danh sach bid", e);
            e.printStackTrace();
            return new BaseResponse().setStatus("FAIL");
        }
    } 
}