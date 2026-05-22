package ddc.server.model.transaction;

import java.time.LocalDateTime;

import ddc.server.model.entity.Entity;
import ddc.server.model.user.User;

public class Bid extends Entity<Bid> {
    private Auction auction;
    private User bidder;
    private double bidAmount;
    private LocalDateTime bidTime;

    public Bid () {}

    //Getters
    public Auction getAuction () { return auction; }
    public User getBidder () { return bidder; }
    public double getBidAmount () { return bidAmount; } 
    public LocalDateTime getBidTime () { return bidTime; } 

    //Setters
    public Bid setAuction (Auction auction) {
        this.auction = auction;
        return this;
    }

    public Bid setBidder (User bidder) {
        this.bidder = bidder;
        return this;
    }

    public Bid setBidAmount (double bidAmount) {
        this.bidAmount = bidAmount;
        return this;
    }

    public Bid setBidTime (LocalDateTime bidTime) {
        this.bidTime = bidTime;
        return this;
    }
}