package ddc.server.controller.handler;

import ddc.server.controller.RequestMessage;
import ddc.server.controller.service.AuctionService;
import ddc.server.model.transaction.Auction;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.Response;

public class CreateAuctionHandler implements ActionHandler {
    private final AuctionService auctionService = new AuctionService();

    @Override
    public Response handle(RequestMessage request) {
        try {
            if (request.getData() == null || request.getData().isBlank()) {
                return new BaseResponse().setStatus("INVALID_INPUT");
            }

            Auction auction = gson.fromJson(request.getData(), Auction.class);
            if (auction == null || auction.getItem() == null || auction.getItem().getId() == null) {
                return new BaseResponse().setStatus("INVALID_INPUT");
            }
            if (auction.getStartTime() == null
                    || auction.getEndTime() == null
                    || !auction.getEndTime().isAfter(auction.getStartTime())
                    || auction.getCurrentPrice() <= 0) {
                return new BaseResponse().setStatus("INVALID_INPUT");
            }

            boolean isSuccess = auctionService.createAuction(auction);
            if (isSuccess) {
                return new BaseResponse().setStatus("SUCCESS");
            }
            return new BaseResponse().setStatus("FAIL");
        } catch (Exception e) {
            return new BaseResponse().setStatus("SERVER_ERROR");
        }
    }
}
