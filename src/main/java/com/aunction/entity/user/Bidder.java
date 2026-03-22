package entity.user;

import java.util.List;
import entity.auction.BidTransaction;

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