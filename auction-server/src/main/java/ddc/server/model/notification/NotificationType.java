package ddc.server.model.notification;

public enum NotificationType {
    BID_OUTBID, // Bị outbid
    AUCTION_WON, // Thắng đấu giá
    AUCTION_ENDED, // Auction kết thúc (cho seller)
    AUCTION_NO_BID // Không ai bid (cho seller)
}
