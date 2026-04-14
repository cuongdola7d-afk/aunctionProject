package ddc.server.model.user;
import java.util.List;

import ddc.server.model.transaction.BidTransaction;
public class Bidder{
    private List<BidTransaction> bidHistory;

    public void addBid(BidTransaction bid) {
        bidHistory.add(bid);
    }
}