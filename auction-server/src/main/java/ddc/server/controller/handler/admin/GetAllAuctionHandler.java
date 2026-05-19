package ddc.server.controller.handler.admin;

import java.util.List;

import ddc.server.controller.RequestMessage;
import ddc.server.controller.handler.ActionHandler;
import ddc.server.model.transaction.Auction;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.GetAllAuctionsResponse;
import ddc.server.network.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAllAuctionHandler implements ActionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetAllAuctionHandler.class);

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
            LOGGER.error("Loi lay danh sach auction", e);
            return new BaseResponse().setStatus("FAIL");
        }
    }
}
