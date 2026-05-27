package ddc.server.controller.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddc.server.controller.RequestMessage;
import ddc.server.model.transaction.Auction;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.GetAllAuctionsResponse;
import ddc.server.network.response.Response;

public class GetAllUserAuctionHandler implements ActionHandler{
    private static final Logger LOGGER = LoggerFactory.getLogger(GetAllUserAuctionHandler.class);

    @Override
    public Response<?> handle (RequestMessage request) {
        try {
            System.out.println(gson.fromJson(request.getData(), String.class));
            List<Auction> auctions = auctionService.getAllUserAuctions(gson.fromJson(request.getData(), String.class));
            
            if (auctions == null) {
                return new BaseResponse().setStatus("FAIL");
            }

            return new GetAllAuctionsResponse().setStatus("SUCCESS")
                                               .setData(auctions);
        } catch (Exception e) {
            LOGGER.error("Loi lay danh sach auction", e);
            return new BaseResponse().setStatus("FAIL");
        }
    }
}
