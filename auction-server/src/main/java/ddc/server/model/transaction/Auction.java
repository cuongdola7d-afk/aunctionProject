package ddc.server.model.transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ddc.server.model.entity.Entity;

public class Auction extends Entity<Auction> {
    private String itemName;
    private final List<Bid> bidHistory = new ArrayList<>();

    private AuctionStatus status = AuctionStatus.OPEN;

    private String highestBidderName;
    private double currentPrice;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    //Getters
    public String getItemName () { return itemName; }
    public List<Bid> getBidHistory () { return bidHistory; }
    public AuctionStatus getStatus () { return status; }
    public String getHighestBidderName() { return highestBidderName; }
    public double getCurrentPrice() { return currentPrice; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }

    //Setters
    public Auction setItemName (String itemName) {
        this.itemName = itemName;
        return this;
    }

    public Auction setStatus (AuctionStatus status) {
        this.status = status;
        return this;
    }

    public Auction setHighestBidderName (String highestBidderName) {
        this.highestBidderName = highestBidderName;
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
}
