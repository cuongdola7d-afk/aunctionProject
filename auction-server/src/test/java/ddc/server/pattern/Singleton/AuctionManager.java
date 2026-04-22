package ddc.server.pattern.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ddc.server.dao.ItemDAO;
import ddc.server.exception.AuctionNotFoundException;
import ddc.server.exception.BidderNotFoundException;
import ddc.server.model.item.ItemGeneric;
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

    // ItemDAO itemDAO = new ItemDAO();

    //     // 2. Chọn một ID chắc chắn đang có trong DB (xem trong MySQL Workbench)
    //     String idCanTest = "I00001"; 

    //     System.out.println("---------- ĐANG TEST GET ITEM ----------");
        
    //     try {
    //         // 3. Gọi hàm getItem
    //         // Hàm này sẽ dùng Factory để tạo object và loadSpecificDetails để lấy data bảng phụ
    //         ItemGeneric item = itemDAO.getItem(idCanTest);

    //         // 4. Kiểm tra và in kết quả
    //         if (item != null) {
    //             System.out.println("Tìm thấy sản phẩm!");
                
    //             // CHỈ CẦN DÒNG NÀY: Java sẽ tự gọi toString() của Art/Electronics tương ứng
    //             System.out.println("Thông tin chi tiết: " + item); 
                
    //         } else {
    //             System.out.println("Không tìm thấy sản phẩm nào với ID: " + idCanTest);
    //             System.out.println("Hãy kiểm tra lại tên cột (item_name, seller_name) trong DAO!");
    //         }
    //     } catch (Exception e) {
    //         System.err.println("Có lỗi xảy ra trong quá trình truy vấn:");
    //         e.printStackTrace();
    //     }
        
    //     System.out.println("----------------------------------------");
    // }
}