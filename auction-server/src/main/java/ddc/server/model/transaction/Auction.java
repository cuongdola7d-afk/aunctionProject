package ddc.server.model.transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ddc.server.model.entity.Entity;
import ddc.server.model.item.ItemGeneric;
import ddc.server.model.user.User;

public class Auction extends Entity<Auction> {
    private String auctionId;
    private ItemGeneric item;
    private List<Bid> bidHistory = new ArrayList<>();

    private AuctionStatus status = AuctionStatus.OPEN;

    private User highestBidder;
    private double currentPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Auction() {
    }

    //Getters
    public ItemGeneric getItem () { return item; }
    public List<Bid> getBidHistory () { return bidHistory; }
    public AuctionStatus getStatus () { return status; }
    public User getHighestBidder() { return highestBidder; }
    public double getCurrentPrice() { return currentPrice; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }

    //Setters
    public Auction setItem (ItemGeneric item) {
        this.item = item;
        return this;
    }

    public Auction setStatus (AuctionStatus status) {
        this.status = status;
        return this;
    }

    public Auction setHighestBidder (User highestBidder) {
        this.highestBidder = highestBidder;
        return this;
    }

    public Auction setCurrentPrice (double currentPrice) {
        this.currentPrice = currentPrice;
        return this;
    }

    public Auction setStartTime (LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public Auction setEndTime (LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public void startAuction() {
        this.status = AuctionStatus.RUNNING;
    }

    public void endAuction() {
        this.status = AuctionStatus.FINISHED;
    }

    public void placeBid(Bid bid) {
        if (status != AuctionStatus.RUNNING) {
            throw new RuntimeException("Auction not running.");
        }

        if (bid.getBidAmount() <= currentPrice) {
            throw new RuntimeException("Bidded amount lower the current.");
        }

        bidHistory.add(bid);
        currentPrice = bid.getBidAmount();
        highestBidder = bid.getBidder();
    }
}