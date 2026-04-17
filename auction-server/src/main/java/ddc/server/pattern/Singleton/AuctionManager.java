// package ddc.server.pattern.Singleton;

// import ddc.server.model.transaction.Auction;
// import ddc.server.model.user.User;

// import java.util.concurrent.ConcurrentHashMap;
// import java.util.Map;

// public class AuctionManager {
//     private static volatile AuctionManager instance;  
//     // 2. Danh sách lưu trữ các phiên đấu giá đang chạy
//     private final Map<String, Auction> auctionList;
//     private final Map<String, User> bidderList;

//     private AuctionManager() {
//         auctionList = new ConcurrentHashMap<>();
//         bidderList = new ConcurrentHashMap<>();
//     }

//     public static AuctionManager getInstance() {
//         if (instance == null) {
//             synchronized (AuctionManager.class) {
//                 if (instance == null) {
//                     instance = new AuctionManager();
//                 }
//             }
//         }
//         return instance;
//     }

    
//     public void addAuction(Auction auction) {
//         auctionList.put(auction.getItem().getId(), auction);
//     }

//     public Auction getAuction(String itemId) {
//         return auctionList.get(itemId);
//     }
   
//     public Map<String, Auction> getAllauctionList() {
//         return auctionList;
//     }

//     public void addBidder(User bidder) {
//         if (bidder != null && bidder.getId() != null) {
//             bidderList.put(bidder.getId(), bidder);
//         }
//     }

//     public User getBidder(String bidderId) {
//         return bidderList.get(bidderId);
//     }

//     public Map<String, User> getAllBidders() {
//         return bidderList;
//     }
// }