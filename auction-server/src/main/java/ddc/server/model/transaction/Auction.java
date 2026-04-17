package ddc.server.model.transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import ddc.server.model.entity.BaseEntity;
import ddc.server.model.item.Item;
import ddc.server.model.user.Bidder;

public class Auction extends BaseEntity {
    private Item item;
    private List<BidTransaction> bids = new ArrayList<>();

    private AuctionStatus status = AuctionStatus.OPEN;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Bidder highestBidder;
    private double currentPrice;

    // Lock riêng cho từng phiên đấu giá
    private final ReentrantLock lock = new ReentrantLock(true);

    public Auction() {
        super();
        this.bids = new ArrayList<>();
        this.status = AuctionStatus.OPEN;
    }

    public Auction(Item item, LocalDateTime startTime, LocalDateTime endTime) {
        super();
        this.item = item;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bids = new ArrayList<>();
        this.status = AuctionStatus.OPEN;
        initializePriceFromItem();
    }

    private void initializePriceFromItem() {
        if (item != null) {
            this.currentPrice = item.getStartingPrice();
            item.setCurrentPrice(item.getStartingPrice());
        }
    }

    public void startAuction() {
        if (status == AuctionStatus.CANCELLED) {
            throw new IllegalStateException("Auction has been canceled");
        }
        if (status == AuctionStatus.FINISHED) {
            throw new IllegalStateException("Auction has already finished");
        }
        this.status = AuctionStatus.RUNNING;
    }

    public void finishAuction() {
        if (status == AuctionStatus.CANCELLED) {
            throw new IllegalStateException("Canceled auction cannot be finished");
        }
        this.status = AuctionStatus.FINISHED;
    }

    public void cancelAuction() {
    if (status == AuctionStatus.FINISHED) {
        throw new IllegalStateException("Phiên đấu giá đã kết thúc, không thể hủy!");
    }
    this.status = AuctionStatus.CANCELLED;
}

    public boolean hasStarted() {
        return startTime != null && !LocalDateTime.now().isBefore(startTime);
    }

    public boolean hasEnded() {
        return endTime != null && !LocalDateTime.now().isBefore(endTime);
    }

    public boolean isRunning() {
        return status == AuctionStatus.RUNNING;
    }

    public boolean canAcceptBid(double amount) {
        return status == AuctionStatus.RUNNING
                && !hasEnded()
                && amount > currentPrice;
    }

 public void placeBid(BidTransaction bid) {
    if (bid == null) {
        throw new IllegalArgumentException("Bid must not be null");
    }

    if (bid.getAuctionId() == null || bid.getAuctionId().isBlank()) {
        bid.setAuctionId(this.getId());
    }

    bids.add(bid);
    currentPrice = bid.getAmount();
    highestBidder = bid.getBidder();

    if (item != null) {
        item.setCurrentPrice(currentPrice);
    }
}
    /*public void addBid(BidTransaction bidTransaction) {
        if (bidTransaction != null) {
            bids.add(bidTransaction);
        }
    }*/

    public void extendTimeIfNeeded() {
        if (endTime != null && endTime.minusSeconds(10).isBefore(LocalDateTime.now())) {
            endTime = endTime.plusSeconds(60);
        }
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        if (item != null && currentPrice <= 0) {
            this.currentPrice = item.getStartingPrice();
            item.setCurrentPrice(item.getStartingPrice());
        }
    }

    public List<BidTransaction> getBids() {
        return bids;
    }

    public void setBids(List<BidTransaction> bids) {
        this.bids = (bids != null) ? bids : new ArrayList<>();
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public void setStatus(AuctionStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Bidder getHighestBidder() {
        return highestBidder;
    }

    public void setHighestBidder(Bidder highestBidder) {
        this.highestBidder = highestBidder;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
        if (item != null) {
            item.setCurrentPrice(currentPrice);
        }
    }

    public ReentrantLock getLock() {
        return lock;
    }

    // Compatibility cho code cũ
    public double getCurrentHighestBid() {
        return currentPrice;
    }

    public void setCurrentHighestBid(double currentHighestBid) {
        setCurrentPrice(currentHighestBid);
    }
}