package ddc.server.model.user;
import java.util.List;
import ddc.server.model.transaction.*;
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