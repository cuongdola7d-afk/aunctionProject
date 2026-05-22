package ddc.server.network.response;

import java.util.List;

import ddc.server.model.transaction.Bid;

public class GetAllUserBidResponse extends Response<GetAllUserBidResponse>{
    private List<Bid> data;

    public List<Bid> getData () { return data; }

    public GetAllUserBidResponse setData (List<Bid> data) {
        this.data = data;
        return this;
    }
}
