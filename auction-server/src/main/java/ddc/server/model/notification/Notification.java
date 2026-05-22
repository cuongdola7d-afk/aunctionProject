package ddc.server.model.notification;

import java.time.LocalDateTime;

// Entity thông báo - map với bảng ddc_notifications
public class Notification {
    private String id;
    private String userId;
    private String type; // enum string: BID_OUTBID, AUCTION_WON, ...
    private String auctionId;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    // Constructor rỗng + getter/setter cho tất cả field
    // Dùng fluent setter (return this) giống pattern Entity<T> hiện có
    public Notification() {
    }

    public String getId() {
        return id;
    }

    public Notification setId(String id) {
        this.id = id;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public Notification setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getType() {
        return type;
    }

    public Notification setType(String type) {
        this.type = type;
        return this;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public Notification setAuctionId(String auctionId) {
        this.auctionId = auctionId;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Notification setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Notification setMessage(String message) {
        this.message = message;
        return this;
    }

    public boolean isRead() {
        return isRead;
    }

    public Notification setRead(boolean isRead) {
        this.isRead = isRead;
        return this;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Notification setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

}
