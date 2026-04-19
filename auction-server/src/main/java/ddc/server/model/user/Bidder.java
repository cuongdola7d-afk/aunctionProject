package ddc.server.model.user;

import java.util.ArrayList;
import java.util.List;

import ddc.server.model.transaction.Bid;

public class Bidder extends User {
    private List<Bid> bidHistory = new ArrayList<>();

    public Bidder() {
        super();
    }

    public void addBid(Bid bid) {
        bidHistory.add(bid);
    }

    public List<Bid> getBidHistory() {
        return bidHistory;
    }
}