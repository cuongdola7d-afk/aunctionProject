package ddc.server.network.response;

import java.util.List;

import ddc.server.model.transaction.Auction;

public class GetAllAuctionsResponse extends Response<GetAllAuctionsResponse>{
    private List<Auction> data;

    public List<Auction> getData () { return data; }

    public GetAllAuctionsResponse setData (List<Auction> data) {
        this.data = data;
        return this;
    }
}
