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
            System.out.println("Creating Auction...");

            if (request.getData() == null) return new BaseResponse().setStatus("FAIL");

            Auction auction = gson.fromJson(request.getData(), Auction.class);
            System.out.println(auction.getItem().getItemName());
            System.out.println(auction.getItem().getId());            
            boolean isSuccess = auctionService.createAuction(auction);

            if (isSuccess) {
                return new BaseResponse().setStatus("SUCCESS");
            } else {
                return new BaseResponse().setStatus("FAIL");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new BaseResponse().setStatus("FAIL");
        }
    }
}
