package ddc.client.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BidTransactionDTO {//Data transfer objects

    private Long bidId;
    private Long auctionId;
    private Long bidderId;
    private String bidderName;
    private BigDecimal bidAmount;
    private LocalDateTime bidTime;

    public BidTransactionDTO() {
    }

    public BidTransactionDTO(Long bidId,
                             Long auctionId,
                             Long bidderId,
                             String bidderName,
                             BigDecimal bidAmount,
                             LocalDateTime bidTime) {
        this.bidId = bidId;
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.bidderName = bidderName;
        this.bidAmount = bidAmount;
        this.bidTime = bidTime;
    }

    public Long getBidId() {
        return bidId;
    }

    public void setBidId(Long bidId) {
        this.bidId = bidId;
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public Long getBidderId() {
        return bidderId;
    }

    public void setBidderId(Long bidderId) {
        this.bidderId = bidderId;
    }

    public String getBidderName() {
        return bidderName;
    }

    public void setBidderName(String bidderName) {
        this.bidderName = bidderName;
    }

    public BigDecimal getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(BigDecimal bidAmount) {
        this.bidAmount = bidAmount;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }

    public void setBidTime(LocalDateTime bidTime) {
        this.bidTime = bidTime;
    }

    @Override
    public String toString() {
        return "BidTransactionDTO{" +
                "bidId=" + bidId +
                ", auctionId=" + auctionId +
                ", bidderId=" + bidderId +
                ", bidderName='" + bidderName + '\'' +
                ", bidAmount=" + bidAmount +
                ", bidTime=" + bidTime +
                '}';
    }
}