package entity.auction;

import entity.base.BaseEntity;
import entity.user.Bidder;
import java.time.LocalDateTime;

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