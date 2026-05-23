package ddc.client.model;

import java.time.LocalDateTime;

public class BidDTO {
    private String id;
    private AuctionDTO auction;
    private UserDTO bidder;
    private double bidAmount;
    private LocalDateTime bidTime;

    public BidDTO () {}

    //Getters
    public String getId () { return id;}
    public AuctionDTO getAuction () { return auction; }
    public UserDTO getBidder () { return bidder; }
    public double getBidAmount () { return bidAmount; } 
    public LocalDateTime getBidTime () { return bidTime; } 

    //Setters
    public BidDTO setId (String id) {
        this.id = id;
        return this;
    }
    
    public BidDTO setAuction (AuctionDTO auction) {
        this.auction = auction;
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