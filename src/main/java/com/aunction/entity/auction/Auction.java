package com.aunction.entity.auction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.aunction.entity.base.BaseEntity;
import com.aunction.entity.item.Item;
import com.aunction.entity.user.Bidder;

public class Auction extends BaseEntity {
    private Item item;
    private List<BidTransaction> bids = new ArrayList<>();
    private AuctionStatus status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Bidder highestBidder;
    private double currentPrice;

    // ===== CORE METHODS =====

    public void startAuction() {
        this.status = AuctionStatus.RUNNING;
    }

    public void endAuction() {
        this.status = AuctionStatus.FINISHED;
    }

    public void placeBid(BidTransaction bid) {
        if (status != AuctionStatus.RUNNING) {
            throw new RuntimeException("Auction not running");
        }

        if (bid.getAmount() <= currentPrice) {
            throw new RuntimeException("Invalid bid");
        }

        bids.add(bid);
        currentPrice = bid.getAmount();
        highestBidder = bid.getBidder();
    }

    // ===== OPTIONAL (BONUS) =====

    public void extendTimeIfNeeded() {
        if (endTime.minusSeconds(10).isBefore(LocalDateTime.now())) {
            endTime = endTime.plusSeconds(60);
        }
    }

    // getters/setters
    public double getCurrencePrice(){
          return currentPrice;
    }
}