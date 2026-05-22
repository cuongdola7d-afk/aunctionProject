package ddc.client.network.request;

public class WalletRequest {
    private String userId;

    public WalletRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
