package ddc.client.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ddc.client.model.ItemDTO.ItemGeneric;

public class AuctionDTO {
    private String auctionId;
    private ItemGeneric item;
    private List<BidDTO> bidHistory = new ArrayList<>();

    private AuctionStatus status = AuctionStatus.OPEN;

    private UserDTO highestBidder;
    private double currentPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public AuctionDTO() {
    }

    //Getters
    public String getAuctionId () { return auctionId; }
    public ItemGeneric getItem () { return item; }
    public List<BidDTO> getBidHistory () { return bidHistory; }
    public AuctionStatus getStatus () { return status; }
    public UserDTO getHighestBidder() { return highestBidder; }
    public double getCurrentPrice() { return currentPrice; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }

    //Setters
    public AuctionDTO setAuctionId (String auctionId) {
        this.auctionId = auctionId;
        return this;
    }

    public AuctionDTO setItem (ItemGeneric item) {
        this.item = item;
        return this;
    }

    public AuctionDTO setStatus (String status) {
        this.status = AuctionStatus.valueOf(status);
        return this;
    }

    public AuctionDTO setHighestBidder (UserDTO highestBidder) {
        this.highestBidder = highestBidder;
        return this;
    }

    public AuctionDTO setCurrentPrice (double currentPrice) {
        this.currentPrice = currentPrice;
        return this;
    }

    public AuctionDTO setStartTime (LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public AuctionDTO setEndTime (LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public void startAuction() {
        this.status = AuctionStatus.RUNNING;
    }

    public void endAuction() {
        this.status = AuctionStatus.FINISHED;
    }

    public void placeBid(BidDTO bid) {
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