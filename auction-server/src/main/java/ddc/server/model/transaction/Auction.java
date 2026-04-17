package ddc.server.model.transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;

import ddc.server.model.entity.Entity;
import ddc.server.model.item.Item;
import ddc.server.model.user.User;

public class Auction extends Entity {
    private Item item;
    private List<Bid> bidHistory = new ArrayList<>();

    private AuctionStatus status = AuctionStatus.OPEN;

    private User highestBidder;
    private double currentPrice;

    private LocalDateTime startTime;
    private LocalDateTime endTime;


    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public List<Bid> getbidHistory() {
        return bidHistory;
    }

    public Bid getCurrentHighestBid() {
        if (bidHistory == null || bidHistory.isEmpty()) {
            return null;
        }
        //tìm ra lệnh đặt giá cao nhất trong danh sách một cách ngắn gọn thay vì phải dùng vòng lặp for
        return bidHistory.stream()
                         .max(Comparator.comparingDouble(Bid::getAmount))
                         .orElse(null);
    }

    public void setStatus(AuctionStatus status) {
        this.status = status;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public User getHighestBidder() {
        return highestBidder;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void startAuction () {
        this.status = AuctionStatus.RUNNING;
    }

    public void endAuction () {
        this.status = AuctionStatus.FINISHED;
    }

    public void placeBid (Bid bid) {
        if (status != AuctionStatus.RUNNING) {
            throw new RuntimeException("Auction not running.");
        }

        if (bid.getAmount() <= currentPrice) {
            throw new RuntimeException("Bidded amount lower the current.");
        }

        bidHistory.add(bid);
        currentPrice = bid.getAmount();
        highestBidder = bid.getBidder();
    }


}
