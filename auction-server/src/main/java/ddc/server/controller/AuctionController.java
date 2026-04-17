package ddc.server.controller;

import ddc.server.dao.ItemDAO;

public class AuctionController {
    
    private final ItemDAO itemDAO = new ItemDAO();

    
}

// // package ddc.server.controller;

// import java.util.List;

// import ddc.server.exception.AuctionClosedException;
// import ddc.server.exception.InvalidBidException;
// import ddc.server.model.transaction.Auction;
// import ddc.server.model.transaction.AuctionStatus;
// import ddc.server.model.transaction.BidTransaction;
// import ddc.server.model.user.Bidder;
// import ddc.server.service.AuctionService;

// // public class AuctionController {
// //     private final AuctionService auctionService;

//     public AuctionController() {
//         this(new AuctionService());
//     }

//     public AuctionController(AuctionService auctionService) {
//         this.auctionService = auctionService;
//     }

// //     public void handleStartAuction(Auction auction) throws AuctionClosedException, InvalidBidException {
// //         auctionService.startAuction(auction);
// //     }

// //     public void handlePlaceBid(Auction auction, Bidder bidder, double amount)
// //             throws InvalidBidException, AuctionClosedException {
// //         auctionService.placeBid(auction, bidder, amount);
// //     }

// //     public void handleFinishAuction(Auction auction) throws InvalidBidException {
// //         auctionService.finishAuction(auction);
// //     }

// //     public AuctionStatus handleRefreshStatus(Auction auction) {
// //         auctionService.refreshAuctionStatus(auction);
// //         return auction.getStatus();
// //     }

// //     public double getCurrentPrice(Auction auction) {
// //         return auctionService.getCurrentPrice(auction);
// //     }

// //     public Bidder getHighestBidder(Auction auction) {
// //         return auctionService.getHighestBidder(auction);
// //     }

//     public List<BidTransaction> getBidHistory(Auction auction) {
//         return auctionService.getBidHistory(auction);
//     }

//     public AuctionService getAuctionService() {
//         return auctionService;
//     }
// }
