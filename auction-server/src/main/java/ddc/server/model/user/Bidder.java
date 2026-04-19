package ddc.server.model.user;

import java.util.ArrayList;
import java.util.List;

import ddc.server.model.transaction.Bid;

public class Bidder {
    private List<Bid> bidHistory = new ArrayList<>();

    public void addBid (Bid bid) {
        bidHistory.add(bid);
    }
}
