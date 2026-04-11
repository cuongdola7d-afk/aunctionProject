package ddc.client.network;

public interface ServerMessageListener {
    void onAuctionEvent(AuctionEventResponse event);
    void onError(String message);
    void onDisconnected(String message);
}