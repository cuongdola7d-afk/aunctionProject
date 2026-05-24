package ddc.server.network.request;

// Payload nhẹ gửi tới tất cả client khi có thay đổi auction (giá, status, endTime)
public class DashboardUpdatePayload {
    private String auctionId;
    private double currentPrice;
    private String status;
    private String endTime;

    public DashboardUpdatePayload() {}

    public DashboardUpdatePayload(String auctionId, double currentPrice, String status, String endTime) {
        this.auctionId = auctionId;
        this.currentPrice = currentPrice;
        this.status = status;
        this.endTime = endTime;
    }

    public String getAuctionId() { return auctionId; }
    public void setAuctionId(String auctionId) { this.auctionId = auctionId; }

    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
