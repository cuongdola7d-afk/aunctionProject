package ddc.server.controller.handler;

import java.util.List;

import ddc.server.controller.RequestMessage;
import ddc.server.dao.AuctionDAO;
import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.AuctionStatus;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.GetAllAuctionsResponse;
import ddc.server.network.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetAllAuctionHandler implements ActionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetAllAuctionHandler.class);
    private final AuctionDAO auctionDAO = new AuctionDAO();

    @Override
    public Response handle(RequestMessage request) {
        try {
            List<Auction> auctions = auctionService.getAll();

            if (auctions == null) {
                return new BaseResponse().setStatus("FAIL");
            }

            // Đồng bộ status theo thời gian thực khi đọc
            for (Auction auction : auctions) {
                AuctionStatus oldStatus = auction.getStatus();
                auctionService.refreshAuctionStatus(auction);

                // Status đổi → cập nhật DB
                if (auction.getStatus() != oldStatus) {
                    auctionDAO.updateAuction(auction);
                    LOGGER.info("Auto-refresh status: {} -> {} (auction={})",
                            oldStatus, auction.getStatus(), auction.getId());
                }
            }

            return new GetAllAuctionsResponse().setStatus("SUCCESS")
                    .setData(auctions);
        } catch (Exception e) {
            LOGGER.error("Loi lay danh sach auction", e);
            return new BaseResponse().setStatus("FAIL");
        }
    }
}
