package ddc.server.model.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ddc.server.model.transaction.BidTransaction;

public class Bidder extends User {
    private List<BidTransaction> bidHistory = new ArrayList<>();

    public void addBid(BidTransaction bid) {
        if (bid != null) {
            bidHistory.add(bid);
        }
    }

    public List<BidTransaction> getBidHistory() {
        return Collections.unmodifiableList(bidHistory);
    }

    public void setBidHistory(List<BidTransaction> bidHistory) {
        this.bidHistory = (bidHistory != null) ? new ArrayList<>(bidHistory) : new ArrayList<>();
    }

    @Override
    public void printInfo() {
        System.out.println("Bidder: " + name);
    }
}