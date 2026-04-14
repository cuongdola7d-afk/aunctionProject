package ddc.server.model.transaction;

import java.time.LocalDateTime;

import ddc.server.model.entity.BaseEntity;
import ddc.server.model.user.Bidder;

public class BidTransaction extends BaseEntity {
    private Bidder bidder;
    private double amount;
    private LocalDateTime bidTime;

    public BidTransaction(Bidder bidder, double amount,LocalDateTime bidTime) {
        this.bidder = bidder;
        this.amount = amount;
        this.bidTime = bidTime;
    }

    public Bidder getBidder() {
        return bidder;
    }

    public void setBidder(Bidder bidder) {
        this.bidder = bidder;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    
    public LocalDateTime getBidTime() {
        return bidTime;
    }

    public void setBidTime(LocalDateTime bidTime) {
        this.bidTime = bidTime;
    }

    @Override
    public String toString() {
        String bidderName = "Unknown";
        return "BidTransaction{" +
                "bidder=" + bidderName +
                ", amount=" + amount +
                ", bidTime=" + bidTime +
                '}';
    }
}