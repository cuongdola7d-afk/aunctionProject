package ddc.server.pattern.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ddc.server.exception.AuctionNotFoundException;
import ddc.server.exception.BidderNotFoundException;
import ddc.server.model.transaction.Auction;
import ddc.server.model.user.Bidder;

/**
 * AuctionManager là Singleton dùng để lưu Auction và Bidder đang hoạt động trong RAM.
 *
 * Mục đích:
 * - Khi client gửi auctionId / bidderId lên server,p
 *   handler có thể lấy nhanh object thật từ đây.
 * - Dùng chung 1 instance cho toàn server để tránh mỗi nơi giữ 1 danh sách riêng.
 *
 * Lưu ý:
 * - Đây là dữ liệu in-memory, không thay thế database.
 * - DB dùng để lưu lâu dài, AuctionManager dùng để truy xuất nhanh lúc chạy realtime.
 */
public class AuctionManager {

    /**
     * Instance duy nhất của AuctionManager.
     */
    private static final AuctionManager INSTANCE = new AuctionManager();

    /**
     * Lưu danh sách auction theo auctionId.
     */
    private final Map<String, Auction> auctions = new ConcurrentHashMap<>();

    /**
     * Lưu danh sách bidder theo bidderId.
     */
    private final Map<String, Bidder> bidders = new ConcurrentHashMap<>();

    /**
     * Constructor private để đảm bảo class này chỉ được dùng qua getInstance().
     */
    private AuctionManager() {
    }

    /**
     * Lấy ra instance duy nhất của AuctionManager.
     */
    public static AuctionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Thêm auction vào bộ nhớ tạm.
     */
    public void addAuction(Auction auction) {
        if (auction != null && auction.getId() != null) {
            auctions.put(auction.getId(), auction);
        }
    }

    /**
     * Thêm bidder vào bộ nhớ tạm.
     */
    public void addBidder(Bidder bidder) {
        if (bidder != null && bidder.getId() != null) {
            bidders.put(bidder.getId(), bidder);
        }
    }

    /**
     * Lấy auction theo id, trả null nếu không tìm thấy.
     */
    public Auction getAuction(String auctionId) {
        return auctions.get(auctionId);
    }

    /**
     * Lấy bidder theo id, trả null nếu không tìm thấy.
     */
    public Bidder getBidder(String bidderId) {
        return bidders.get(bidderId);
    }

    /**
     * Lấy auction theo id, nếu không có thì ném exception.
     */
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

    /**
     * Lấy bidder theo id, nếu không có thì ném exception.
     */
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