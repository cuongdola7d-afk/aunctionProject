package ddc.server.model.user;

import java.util.ArrayList;
import java.util.List;

import ddc.server.model.transaction.Bid;

public class Bidder extends User {
    private final List<Bid> bidHistory = new ArrayList<>();

    public List<Bid> getBidHistory() { return bidHistory; }

    public void addBid(Bid bid) {
        bidHistory.add(bid);
    }

    
}