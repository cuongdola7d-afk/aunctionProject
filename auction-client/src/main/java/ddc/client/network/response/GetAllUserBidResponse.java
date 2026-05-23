package ddc.client.network.response;

import ddc.client.model.BidDTO;

public class GetAllUserBidResponse extends Response<GetAllUserBidResponse>{
    private BidDTO[] data;

    public BidDTO[] getData () { return data; }

    public GetAllUserBidResponse setData (BidDTO[] data) {
        this.data = data;
        return this;
    }
}
