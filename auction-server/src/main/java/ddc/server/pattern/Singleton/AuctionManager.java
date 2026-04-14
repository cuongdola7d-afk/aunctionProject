package ddc.server.pattern.Singleton;

import ddc.server.model.transaction.Auction;
import ddc.server.model.user.Bidder;
import ddc.server.exception.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class AuctionManager {
    private static volatile AuctionManager instance;  
    // 2. Danh sách lưu trữ các phiên đấu giá đang chạy
    private final Map<String, Auction> auctionStore;
    private final Map<String, Bidder> bidderStore;

    private AuctionManager() {
        auctionStore = new ConcurrentHashMap<>();
        bidderStore = new ConcurrentHashMap<>();
    }

    public static AuctionManager getInstance() {
        if (instance == null) {
            synchronized (AuctionManager.class) {
                if (instance == null) {
                    instance = new AuctionManager();
                }
            }
        }
        return instance;
    }

    
    public void addAuction(Auction auction) {
        auctionStore.put(auction.getItem().getId(), auction);
    }

    public Auction getAuction(String itemId) {
        return auctionStore.get(itemId);
    }
   
    public Map<String, Auction> getAllauctionStore() {
        return auctionStore;
    }

    public void addBidder(Bidder bidder) {
        if (bidder != null && bidder.getId() != null) {
            bidderStore.put(bidder.getId(), bidder);
        }
    }

    public Bidder getBidder(String bidderId) {
        return bidderStore.get(bidderId);
    }

    public Map<String, Bidder> getAllBidders() {
        return bidderStore;
    }
}