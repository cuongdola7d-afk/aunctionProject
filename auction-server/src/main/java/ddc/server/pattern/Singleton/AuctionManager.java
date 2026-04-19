package ddc.server.pattern.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ddc.server.exception.AuctionNotFoundException;
import ddc.server.exception.BidderNotFoundException;
import ddc.server.model.transaction.Auction;
import ddc.server.model.user.Bidder;

public class AuctionManager {
    private static final AuctionManager INSTANCE = new AuctionManager();

    private final Map<String, Auction> auctions = new ConcurrentHashMap<>();
    private final Map<String, Bidder> bidders = new ConcurrentHashMap<>();

    private AuctionManager() {
    }

    public static AuctionManager getInstance() {
        return INSTANCE;
    }

    public void addAuction(Auction auction) {
        if (auction != null && auction.getId() != null) {
            auctions.put(auction.getId(), auction);
        }
    }

    public void addBidder(Bidder bidder) {
        if (bidder != null && bidder.getId() != null) {
            bidders.put(bidder.getId(), bidder);
        }
    }

    public Auction getAuction(String auctionId) {
        return auctions.get(auctionId);
    }

    public Bidder getBidder(String bidderId) {
        return bidders.get(bidderId);
    }

    public Auction getAuctionOrThrow(String auctionId) throws AuctionNotFoundException {
        if (auctionId == null || auctionId.isBlank()) {
            throw new AuctionNotFoundException("auctionId không hợp lệ.");
        }

        Auction auction = auctions.get(auctionId);
        if (auction == null) {
            throw new AuctionNotFoundException("Không tìm thấy phiên đấu giá: " + auctionId);
        }

        return auction;
    }

    public Bidder getBidderOrThrow(String bidderId) throws BidderNotFoundException {
        if (bidderId == null || bidderId.isBlank()) {
            throw new BidderNotFoundException("bidderId không hợp lệ.");
        }

        Bidder bidder = bidders.get(bidderId);
        if (bidder == null) {
            throw new BidderNotFoundException("Không tìm thấy bidder: " + bidderId);
        }

        return bidder;
    }
}
