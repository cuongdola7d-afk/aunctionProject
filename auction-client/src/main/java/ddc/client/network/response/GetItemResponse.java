package ddc.client.network.response;

public class GetItemResponse extends BaseResponse {
    private final String itemJson;

    public String getItemJson () { return itemJson; }

    public GetItemResponse (String status, String itemJson) {
        super(status);
        this.itemJson = itemJson;
    }
}
