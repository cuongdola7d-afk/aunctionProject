package ddc.server.network.response;

public class GetItemResponse extends Response<GetItemResponse> {
    private String itemJson;

    public String getItemJson () { return itemJson; }

    public GetItemResponse setItemJson (String itemJson) {
        this.itemJson = itemJson;
        return this;
    }
}
