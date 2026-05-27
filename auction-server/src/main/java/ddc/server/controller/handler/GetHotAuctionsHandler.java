package ddc.server.controller.handler;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddc.server.controller.RequestMessage;
import ddc.server.model.transaction.Auction;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.GetAllAuctionsResponse;
import ddc.server.network.response.Response;

public class GetHotAuctionsHandler implements ActionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetHotAuctionsHandler.class);

    @Override
    public Response<?> handle(RequestMessage request) {
        try {
            List<Auction> hotList = auctionService.getHotAuctions();

            if (hotList == null) {
                return new BaseResponse().setStatus("FAIL");
            }

            // Ở đây dùng chung với GetAllAuctions Response vì 2 đứa có cùng cấu trúc.
            return new GetAllAuctionsResponse().setStatus("SUCCESS")
                                               .setData(hotList);
        } catch (Exception e) {
            LOGGER.error("Loi lay danh sach auction", e);
            return new BaseResponse().setStatus("FAIL");
        }
    }
}
