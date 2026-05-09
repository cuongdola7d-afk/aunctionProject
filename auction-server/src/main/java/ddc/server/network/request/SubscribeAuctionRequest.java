package ddc.server.network.request;

// DTO nhận từ client khi subscribe auction
public class SubscribeAuctionRequest {
    private String auctionId;

    public SubscribeAuctionRequest() {}

    public String getAuctionId() { return auctionId; }
    public void setAuctionId(String auctionId) { this.auctionId = auctionId; }
}
