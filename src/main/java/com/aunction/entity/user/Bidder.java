package com.aunction.entity.user;

import java.util.List;

import com.aunction.entity.auction.BidTransaction;

public class Bidder extends User {
    private List<BidTransaction> bidHistory;

    public void addBid(BidTransaction bid) {
        bidHistory.add(bid);
    }

    @Override
    public void printInfo() {
        System.out.println("Bidder: " + name);
    }
}