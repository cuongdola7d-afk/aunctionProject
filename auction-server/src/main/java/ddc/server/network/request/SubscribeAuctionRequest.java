package ddc.server.network.request;

public class SubscribeAuctionRequest {
    private String auctionId;

    public SubscribeAuctionRequest() {
    }

    public SubscribeAuctionRequest(String auctionId) {
        this.auctionId = auctionId;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }
}