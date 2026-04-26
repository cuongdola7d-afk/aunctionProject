package ddc.client.model;

import java.time.LocalDateTime;

public class BidDTO {
    private String auctionId;
    private UserDTO bidder;
    private double bidAmount;
    private LocalDateTime bidTime;

    public BidDTO () {}

    //Getters
    public String auctionId () { return auctionId; }
    public UserDTO getBidder () { return bidder; }
    public double getBidAmount () { return bidAmount; } 
    public LocalDateTime getBidTime () { return bidTime; } 

    //Setters
    public BidDTO setAuctionId (String auctionId) {
        this.auctionId = auctionId;
        return this;
    }

    public BidDTO setBidder (UserDTO bidder) {
        this.bidder = bidder;
        return this;
    }

    public BidDTO setBidAmount (double bidAmount) {
        this.bidAmount = bidAmount;
        return this;
    }

    public BidDTO setBidTime (LocalDateTime bidTime) {
        this.bidTime = bidTime;
        return this;
    }
}