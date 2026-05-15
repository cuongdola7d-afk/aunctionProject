package ddc.server.model.transaction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ddc.server.model.entity.Entity;
import ddc.server.model.item.ItemGeneric;
import ddc.server.model.user.User;

public class Auction extends Entity<Auction> {
    private ItemGeneric item;
    private List<Bid> bidHistory = new ArrayList<>();

    private AuctionStatus status = AuctionStatus.OPEN;

    private User highestBidder;
    private double currentPrice;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int antiSnipThresholdSeconds = 60; // mặc định <= 60s sẽ gia hạn thêm 30s
    private int antiSnipExtensionSeconds = 30; // thời gian gia hạn khi bid gần hết thời gian

    public Auction() {
    }

    // Getters
    public ItemGeneric getItem() {
        return item;
    }

    public List<Bid> getBidHistory() {
        return bidHistory;
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

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public int getAntiSnipThresholdSeconds() {
        return antiSnipThresholdSeconds;
    }

    public int getAntiSnipExtensionSeconds() {
        return antiSnipExtensionSeconds;
    }

    // Setters
    public Auction setItem(ItemGeneric item) {
        this.item = item;
        return this;
    }

    public Auction setStatus(String status) {
        this.status = AuctionStatus.valueOf(status);
        return this;
    }

    public Auction setHighestBidder(User highestBidder) {
        this.highestBidder = highestBidder;
        return this;
    }

    public Auction setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
        return this;
    }

    public Auction setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public Auction setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public void startAuction() {
        this.status = AuctionStatus.RUNNING;
    }

    public void endAuction() {
        this.status = AuctionStatus.FINISHED;
    }

    public void placeBid(Bid bid) {
        if (status != AuctionStatus.RUNNING) {
            throw new RuntimeException("Auction not running.");
        }

        if (bid.getBidAmount() <= currentPrice) {
            throw new RuntimeException("Bidded amount lower the current.");
        }

        bidHistory.add(bid);
        currentPrice = bid.getBidAmount();
        highestBidder = bid.getBidder();
    }

    // Config thời gian để gia hạn/gia hạn cho các phiên đấu giá khác nhau nếu không
    // muốn dùng giá trị mặc định
    public Auction setAntiSnipThresholdSeconds(int seconds) {
        this.antiSnipThresholdSeconds = seconds;
        return this;
    }

    public Auction setAntiSnipExtensionSeconds(int seconds) {
        this.antiSnipExtensionSeconds = seconds;
        return this;
    }
}