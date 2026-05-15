package ddc.server.network.request;

// Payload gửi cho client khi có event auction (snapshot hoặc new bid)
public class AuctionEventPayload {
    private String eventType; // "SNAPSHOT" hoặc "NEW_BID"
    private String auctionId;
    private double currentPrice;
    private String status;
    private String bidderName; // tên người bid cao nhất / người vừa bid
    private double bidAmount; // số tiền bid (chỉ dùng cho NEW_BID)
    private String startTime;
    private String endTime;
    private String message;
    private boolean timeExtended; // true nếu endTime đã bị gia hạn bởi anti-snip

    public AuctionEventPayload() {
    }

    // --- Getters & Setters cho tất cả fields ---
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isTimeExtended() {
        return timeExtended;
    }

    public void setTimeExtended(boolean timeExtended) {
        this.timeExtended = timeExtended;
    }
}
