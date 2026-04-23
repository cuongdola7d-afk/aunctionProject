package ddc.server.network.response;

public class AddItemResponse extends Response<AddItemResponse> {
    private String id;

    public String getId () { return id; }

    public AddItemResponse setId (String id) {
        this.id = id;
        return this;
    }
}
