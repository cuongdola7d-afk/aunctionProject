package ddc.client.network.response;

public class AddItemResponse extends BaseResponse {
    private final String id;

    public String getId () { return id; }

    public AddItemResponse (String status, String id) {
        super(status);
        this.id = id;
    }
}
