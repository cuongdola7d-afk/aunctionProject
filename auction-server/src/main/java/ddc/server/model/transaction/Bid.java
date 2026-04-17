package ddc.server.model.transaction;

import java.time.LocalDateTime;

import ddc.server.model.entity.Entity;

public class Bid extends Entity<Bid> {
    private String bidderName;
    private double bidAmount;
    private LocalDateTime bidTime;

    public Bid () {}

    //Getters
    public String getBidderName () { return bidderName; }
    public double getBidAmount () { return bidAmount; } 
    public LocalDateTime getBidTime () { return bidTime; } 

    //Setters
    public Bid setBidderName (String bidderName) {
        this.bidderName = bidderName;
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