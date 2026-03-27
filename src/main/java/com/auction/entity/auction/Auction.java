package com.auction.entity.auction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.auction.entity.base.BaseEntity;
import com.auction.entity.item.Item;
import com.auction.entity.user.Bidder;

public class Auction extends BaseEntity{
    private Item item;
    private List<BidTransaction> bids = new ArrayList<>();

    private AuctionStatus status = AuctionStatus.OPEN;


    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Bidder highestBidder;
    private double currentPrice;
     private double currentHighestBid;

    // ===== CORE METHODS =====

    public void startAuction() {
        this.status = AuctionStatus.RUNNING;
    }

    public void endAuction() {
        this.status = AuctionStatus.FINISHED;
    }

    public void placeBid(BidTransaction bid) {
        if (status != AuctionStatus.RUNNING) {
            throw new RuntimeException("Auction not running");
        }

        if (bid.getAmount() <= currentPrice) {
            throw new RuntimeException("Invalid bid");
        }

        bids.add(bid);
        currentPrice = bid.getAmount();
        highestBidder = bid.getBidder();
    }

    // ===== OPTIONAL (BONUS) =====

    public void extendTimeIfNeeded() {
        if (endTime.minusSeconds(10).isBefore(LocalDateTime.now())) {
            endTime = endTime.plusSeconds(60);
        }
    }

    // ===== GETTERS & SETTERS =====

    public Auction() {
    }

    public Auction(Item item, LocalDateTime startTime, LocalDateTime endTime) {
        this.item = item;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = AuctionStatus.OPEN;

        if (item != null) {
            this.currentHighestBid = item.getStartingPrice();
            item.setCurrentPrice(item.getStartingPrice());
        }
    }

    public boolean hasStarted() {
        return startTime != null && !LocalDateTime.now().isBefore(startTime);
    }

    public boolean hasEnded() {
        return endTime != null && !LocalDateTime.now().isBefore(endTime);
    }

    public void addBid(BidTransaction bidTransaction) {
        if (bidTransaction != null) {
            bids.add(bidTransaction);
        }
    }


    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
        if (item != null && this.currentHighestBid == 0) {
            this.currentHighestBid = item.getStartingPrice();
            item.setCurrentPrice(item.getStartingPrice());
        }
    }

    public List<BidTransaction> getBids() {
        return bids;
    }

    public void setBids(List<BidTransaction> bids) {
        this.bids = bids;
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
    }
    public double getCurrentHighestBid() {
        return currentHighestBid;
    }

    public void setCurrentHighestBid(double currentHighestBid) {
        this.currentHighestBid = currentHighestBid;
    }    
}