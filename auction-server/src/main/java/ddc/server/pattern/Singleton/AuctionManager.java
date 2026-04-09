package ddc.server.pattern.Singleton;

import ddc.server.model.transaction.Auction;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class AuctionManager {
    private static volatile AuctionManager instance;  
    // 2. Danh sách lưu trữ các phiên đấu giá đang chạy
    private Map<String, Auction> auctions;

    private AuctionManager() {
        auctions = new ConcurrentHashMap<>();
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
        auctions.put(auction.getItem().getId(), auction);
    }

    public Auction getAuction(String itemId) {
        return auctions.get(itemId);
    }

    public Map<String, Auction> getAllAuctions() {
        return auctions;
    }
}