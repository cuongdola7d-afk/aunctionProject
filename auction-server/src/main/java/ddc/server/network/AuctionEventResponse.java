package ddc.server.network;

import ddc.server.model.transaction.Auction;
import ddc.server.model.user.Bidder;
import ddc.server.pattern.observer.AuctionEvent;

public class AuctionEventResponse {
    private String eventType;
    private String auctionId;
    private String itemId;
    private String itemName;
    private String bidderName;
    private double bidAmount;
    private double currentPrice;
    private String status;
    private String eventTime;
    private String message;

    public AuctionEventResponse() {
    }

    public static AuctionEventResponse fromAuctionEvent(AuctionEvent event) {
        AuctionEventResponse response = new AuctionEventResponse();
        response.setEventType(event.getType() != null ? event.getType().name() : null);
        response.setAuctionId(event.getAuctionId());
        response.setItemId(event.getItemId());
        response.setItemName(event.getItemName());
        response.setBidderName(event.getBidderName());
        response.setBidAmount(event.getBidAmount());
        response.setCurrentPrice(event.getCurrentPrice());
        response.setStatus(event.getStatus() != null ? event.getStatus().name() : null);
        response.setEventTime(event.getEventTime() != null ? event.getEventTime().toString() : null);
        response.setMessage(event.getMessage());
        return response;
    }

    public static AuctionEventResponse fromAuctionState(Auction auction) {
        AuctionEventResponse response = new AuctionEventResponse();
        response.setEventType("SNAPSHOT");
        response.setAuctionId(auction.getId());
        response.setItemId(auction.getItem() != null ? auction.getItem().getId() : null);
        response.setItemName(auction.getItem() != null ? auction.getItem().getName() : null);

        Bidder highestBidder = auction.getHighestBidder();
        response.setBidderName(highestBidder != null ? highestBidder.getName() : null);

        response.setBidAmount(0);
        response.setCurrentPrice(auction.getCurrentPrice());
        response.setStatus(auction.getStatus() != null ? auction.getStatus().name() : null);
        response.setEventTime(java.time.LocalDateTime.now().toString());
        response.setMessage("Current auction state");
        return response;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}