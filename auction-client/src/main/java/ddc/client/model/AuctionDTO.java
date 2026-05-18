package ddc.client.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ddc.client.model.ItemDTO.ItemGeneric;

public class AuctionDTO {
    private String id;
    private String auctionId;
    private ItemGeneric item;
    private List<BidDTO> bidHistory = new ArrayList<>();

    private AuctionStatus status = AuctionStatus.OPEN;

    private UserDTO highestBidder;
    private double currentPrice;
    private double startingPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public AuctionDTO() {
    }

    //Getters
    public String getId () { return id; }
    public String getAuctionId () { return isBlank(auctionId) ? id : auctionId; }
    public ItemGeneric getItem () { return item; }
    public List<BidDTO> getBidHistory () { return bidHistory; }
    public AuctionStatus getStatus () { return status; }
    public UserDTO getHighestBidder() { return highestBidder; }
    public double getCurrentPrice() { return currentPrice; }
    public double getStartingPrice() { return startingPrice; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }

    //Setters
    public AuctionDTO setAuctionId (String auctionId) {
        this.auctionId = auctionId;
        if (isBlank(this.id)){
               this.id = auctionId;
        } 
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

    public AuctionDTO setStartingPrice (double startingPrice) {
        this.startingPrice = startingPrice;
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

    public AuctionDTO setId (String id){
        this.id = id;
        if (isBlank(this.auctionId)) {
             this.auctionId = id;
        }

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

    private boolean isBlank(String value){
          return value == null || value.trim().isEmpty();
    }
}