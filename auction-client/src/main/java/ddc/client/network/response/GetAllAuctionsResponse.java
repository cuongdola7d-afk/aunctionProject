package ddc.client.network.response;

import ddc.client.model.AuctionDTO;

public class GetAllAuctionsResponse extends Response<GetAllAuctionsResponse>{
    private AuctionDTO[] data;

    public AuctionDTO[] getData () { return data; }

    public GetAllAuctionsResponse setData (AuctionDTO[] data) {
        this.data = data;
        return this;
    }
}
