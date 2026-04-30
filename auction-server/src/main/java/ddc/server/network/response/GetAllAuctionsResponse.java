package ddc.server.network.response;

public class GetAllAuctionsResponse extends Response<GetAllAuctionsResponse>{
    private Object data;

    public Object getData () { return data; }

    public GetAllAuctionsResponse setData (Object data) {
        this.data = data;
        return this;
    }
}
