package ddc.server.pattern.observer;

import java.time.LocalDateTime;

import ddc.server.model.item.Item;
import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.AuctionStatus;
import ddc.server.model.transaction.BidTransaction;
import ddc.server.model.user.Bidder;

public class AuctionEvent {
    private AuctionEventType type;
    private String auctionId;
    private String itemId;
    private String itemName;
    private String bidderName;
    private double bidAmount;
    private double currentPrice;
    private AuctionStatus status;
    private LocalDateTime eventTime;
    private String message;

    public AuctionEvent() {
    }

    public AuctionEvent(
            AuctionEventType type,
            String auctionId,
            String itemId,
            String itemName,
            String bidderName,
            double bidAmount,
            double currentPrice,
            AuctionStatus status,
            LocalDateTime eventTime,
            String message
    ) {
        this.type = type;
        this.auctionId = auctionId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.bidderName = bidderName;
        this.bidAmount = bidAmount;
        this.currentPrice = currentPrice;
        this.status = status;
        this.eventTime = eventTime;
        this.message = message;
    }

    public static AuctionEvent auctionStarted(Auction auction) {
        return baseEvent(
                AuctionEventType.AUCTION_STARTED,
                auction,
                null,
                "Auction started"
        );
    }

    public static AuctionEvent newBid(Auction auction, BidTransaction bid) {
        String bidderName = null;
        double bidAmount = 0;

        if (bid != null) {
            Bidder bidder = bid.getBidder();
            bidderName = (bidder != null) ? bidder.getName() : null;
            bidAmount = bid.getAmount();
        }

        return new AuctionEvent(
                AuctionEventType.NEW_BID,
                safeAuctionId(auction),
                safeItemId(auction),
                safeItemName(auction),
                bidderName,
                bidAmount,
                safeCurrentPrice(auction),
                safeStatus(auction),
                LocalDateTime.now(),
                "New bid placed"
        );
    }

    public static AuctionEvent auctionFinished(Auction auction) {
        return baseEvent(
                AuctionEventType.AUCTION_FINISHED,
                auction,
                null,
                "Auction finished"
        );
    }

    public static AuctionEvent auctionCANCELLED(Auction auction) {
        return baseEvent(
                AuctionEventType.AUCTION_CANCELLED,
                auction,
                null,
                "Auction CANCELLED"
        );
    }

    public static AuctionEvent statusChanged(
            Auction auction,
            AuctionStatus oldStatus,
            AuctionStatus newStatus
    ) {
        return new AuctionEvent(
                AuctionEventType.STATUS_CHANGED,
                safeAuctionId(auction),
                safeItemId(auction),
                safeItemName(auction),
                null,
                0,
                safeCurrentPrice(auction),
                newStatus,
                LocalDateTime.now(),
                "Status changed from " + oldStatus + " to " + newStatus
        );
    }

    private static AuctionEvent baseEvent(
            AuctionEventType type,
            Auction auction,
            String bidderName,
            String message
    ) {
        return new AuctionEvent(
                type,
                safeAuctionId(auction),
                safeItemId(auction),
                safeItemName(auction),
                bidderName,
                0,
                safeCurrentPrice(auction),
                safeStatus(auction),
                LocalDateTime.now(),
                message
        );
    }

    private static String safeAuctionId(Auction auction) {
        return (auction != null) ? auction.getId() : null;
    }

    private static String safeItemId(Auction auction) {
        if (auction == null) {
            return null;
        }
        Item item = auction.getItem();
        return (item != null) ? item.getId() : null;
    }

    private static String safeItemName(Auction auction) {
        if (auction == null) {
            return null;
        }
        Item item = auction.getItem();
        return (item != null) ? item.getName() : null;
    }

    private static double safeCurrentPrice(Auction auction) {
        return (auction != null) ? auction.getCurrentPrice() : 0;
    }

    private static AuctionStatus safeStatus(Auction auction) {
        return (auction != null) ? auction.getStatus() : null;
    }

    public AuctionEventType getType() {
        return type;
    }

    public void setType(AuctionEventType type) {
        this.type = type;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getBidderName() {
        return bidderName;
    }

    public void setBidderName(String bidderName) {
        this.bidderName = bidderName;
    }

    public double getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(double bidAmount) {
        this.bidAmount = bidAmount;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public void setStatus(AuctionStatus status) {
        this.status = status;
    }

    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public void setEventTime(LocalDateTime eventTime) {
        this.eventTime = eventTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AuctionEvent{" +
                "type=" + type +
                ", auctionId='" + auctionId + '\'' +
                ", itemId='" + itemId + '\'' +
                ", itemName='" + itemName + '\'' +
                ", bidderName='" + bidderName + '\'' +
                ", bidAmount=" + bidAmount +
                ", currentPrice=" + currentPrice +
                ", status=" + status +
                ", eventTime=" + eventTime +
                ", message='" + message + '\'' +
                '}';
    }
}