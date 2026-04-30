package ddc.server.controller.handler;

import java.util.List;

import ddc.server.controller.RequestMessage;
import ddc.server.model.transaction.Auction;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.GetAllAuctionsResponse;
import ddc.server.network.response.Response;

public class GetAllAuctionHandler implements ActionHandler{
    @Override
    public Response handle(RequestMessage request) {
        try {
            List<Auction> auctions = auctionService.getAll();

            if (auctions == null) {
                return new BaseResponse().setStatus("FAIL");
            }

            return new GetAllAuctionsResponse().setStatus("SUCCESS")
                                            .setData(auctions);
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse().setStatus("FAIL");
        }
    }
}
