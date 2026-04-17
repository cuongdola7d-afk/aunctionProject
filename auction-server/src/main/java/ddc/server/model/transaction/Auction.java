package ddc.server.model.transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ddc.server.model.item.Item;
import ddc.server.model.user.User;

public class Auction {
    private String id;
    private Item item;
    private List<Bid> bids = new ArrayList<>();

    private AuctionStatus status = AuctionStatus.OPEN;

    private User highestBidder;
    private double currentPrice;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public User getHighestBidder() {
        return highestBidder;
    }

    public double getCurrentPrice() {
        return currentPrice;
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

    public void startAuction () {
        this.status = AuctionStatus.RUNNING;
    }

    public void endAuction () {
        this.status = AuctionStatus.FINISHED;
    }

    public void placeBid (Bid bid) {
        if (status != AuctionStatus.RUNNING) {
            throw new RuntimeException("Auction not running.");
        }

        if (bid.getAmount() <= currentPrice) {
            throw new RuntimeException("Bidded amount lower the current.");
        }

        bids.add(bid);
        currentPrice = bid.getAmount();
        highestBidder = bid.getBidder();
    }


}
