package ddc.client.network.response;

// Payload nhẹ chứa thông tin cập nhật dashboard (giá, status, endTime)
public class DashboardUpdatePayload {
    private String auctionId;
    private double currentPrice;
    private String status;
    private String endTime;

    public DashboardUpdatePayload() {}

    public String getAuctionId() { return auctionId; }
    public void setAuctionId(String auctionId) { this.auctionId = auctionId; }

    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
