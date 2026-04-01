package ddc.client.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuctionDTO {

    private Long auctionId;
    private Long itemId;
    private String itemName;
    private String description;

    private BigDecimal startingPrice;
    private BigDecimal currentPrice;
    private BigDecimal minimumNextBid;

    private Long highestBidderId;
    private String highestBidderName;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String status; // OPEN, RUNNING, FINISHED, PAID, CANCELED

    public AuctionDTO() {
    }

    public AuctionDTO(Long auctionId,
                      Long itemId,
                      String itemName,
                      String description,
                      BigDecimal startingPrice,
                      BigDecimal currentPrice,
                      BigDecimal minimumNextBid,
                      Long highestBidderId,
                      String highestBidderName,
                      LocalDateTime startTime,
                      LocalDateTime endTime,
                      String status) {
        this.auctionId = auctionId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.description = description;
        this.startingPrice = startingPrice;
        this.currentPrice = currentPrice;
        this.minimumNextBid = minimumNextBid;
        this.highestBidderId = highestBidderId;
        this.highestBidderName = highestBidderName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(BigDecimal startingPrice) {
        this.startingPrice = startingPrice;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getMinimumNextBid() {
        return minimumNextBid;
    }

    public void setMinimumNextBid(BigDecimal minimumNextBid) {
        this.minimumNextBid = minimumNextBid;
    }

    public Long getHighestBidderId() {
        return highestBidderId;
    }

    public void setHighestBidderId(Long highestBidderId) {
        this.highestBidderId = highestBidderId;
    }

    public String getHighestBidderName() {
        return highestBidderName;
    }

    public void setHighestBidderName(String highestBidderName) {
        this.highestBidderName = highestBidderName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isRunning() {
        return "RUNNING".equalsIgnoreCase(status);
    }

    public boolean isFinished() {
        return "FINISHED".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "AuctionDTO{" +
                "auctionId=" + auctionId +
                ", itemId=" + itemId +
                ", itemName='" + itemName + '\'' +
                ", description='" + description + '\'' +
                ", startingPrice=" + startingPrice +
                ", currentPrice=" + currentPrice +
                ", minimumNextBid=" + minimumNextBid +
                ", highestBidderId=" + highestBidderId +
                ", highestBidderName='" + highestBidderName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status='" + status + '\'' +
                '}';
    }
}