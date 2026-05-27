package ddc.client.network.response;

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
    private String startTime;
    private String endTime;
    private boolean timeExtended;
    private long minBidIncrement;

    public AuctionEventResponse() {
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isTimeExtended() {
        return timeExtended;
    }

    public void setTimeExtended(boolean timeExtended) {
        this.timeExtended = timeExtended;
    }

    public long getMinBidIncrement() {
        return minBidIncrement;
    }

    public void setMinBidIncrement(long minBidIncrement) {
        this.minBidIncrement = minBidIncrement;
    }
}