package ddc.server.network.message;

public enum MessageType {
    AUTH,
    SUBSCRIBE_AUCTION,
    PLACE_BID,
    AUCTION_EVENT,
    NOTIFICATION_EVENT,
    DASHBOARD_UPDATE,
    DASHBOARD_REFRESH,
    ERROR,
    PING
}