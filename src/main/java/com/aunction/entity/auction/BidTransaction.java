package com.aunction.entity.auction;

import java.time.LocalDateTime;

import com.aunction.entity.base.BaseEntity;
import com.aunction.entity.user.Bidder;

public class BidTransaction extends BaseEntity {
    private Bidder bidder;
    private double amount;
    private LocalDateTime time;

    public BidTransaction(Bidder bidder, double amount) {
        this.bidder = bidder;
        this.amount = amount;
        this.time = LocalDateTime.now();
    }

    public double getAmount() {
        return amount;
    }

    public Bidder getBidder() {
        return bidder;
    }
}