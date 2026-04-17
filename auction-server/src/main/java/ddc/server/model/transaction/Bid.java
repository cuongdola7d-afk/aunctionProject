package ddc.server.model.transaction;

import java.time.LocalDateTime;

import ddc.server.model.user.User;

public class Bid {
    private User bidder;
    private double amount;
    private LocalDateTime time;

    public Bid (User bidder, double amount) {
        this.bidder = bidder;
        this.amount = amount;
        this.time = LocalDateTime.now();
    }

    public User getBidder () {
        return bidder;
    }

    public double getAmount () {
        return amount;
    } 

    public LocalDateTime getTime () {
        return time;
    } 
}