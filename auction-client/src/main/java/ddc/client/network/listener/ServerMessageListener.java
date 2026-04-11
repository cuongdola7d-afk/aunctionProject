package ddc.client.network.listener;

import ddc.client.network.response.AuctionEventResponse;

public interface ServerMessageListener {
    void onAuctionEvent(AuctionEventResponse event);
    void onError(String message);
    void onDisconnected(String message);
}