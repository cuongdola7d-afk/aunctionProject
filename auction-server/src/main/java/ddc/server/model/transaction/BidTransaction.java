// package ddc.server.model.transaction;

// import java.time.LocalDateTime;

// import ddc.server.model.entity.BaseEntity;
// import ddc.server.model.user.Bidder;

// public class BidTransaction extends BaseEntity {
//     private String auctionId;
//     private Bidder bidder;
//     private double amount;
//     private LocalDateTime bidTime;

//     public BidTransaction() {
//     }

//     public BidTransaction(Bidder bidder, double amount, LocalDateTime bidTime) {
//         this.bidder = bidder;
//         this.amount = amount;
//         this.bidTime = (bidTime != null) ? bidTime : LocalDateTime.now();
//     }

//     public BidTransaction(String auctionId, Bidder bidder, double amount, LocalDateTime bidTime) {
//         this.auctionId = auctionId;
//         this.bidder = bidder;
//         this.amount = amount;
//         this.bidTime = (bidTime != null) ? bidTime : LocalDateTime.now();
//     }

//     public String getAuctionId() {
//         return auctionId;
//     }

//     public void setAuctionId(String auctionId) {
//         this.auctionId = auctionId;
//     }

//     public Bidder getBidder() {
//         return bidder;
//     }

//     public void setBidder(Bidder bidder) {
//         this.bidder = bidder;
//     }

//     public double getAmount() {
//         return amount;
//     }

//     public void setAmount(double amount) {
//         this.amount = amount;
//     }

//     public LocalDateTime getBidTime() {
//         return bidTime;
//     }

//     public void setBidTime(LocalDateTime bidTime) {
//         this.bidTime = bidTime;
//     }

//     public boolean isHigherThan(double currentPrice) {
//         return amount > currentPrice;
//     }

//     @Override
//     public String toString() {
//         String bidderName = "Unknown";
//         return "BidTransaction{" +
//                 "auctionId='" + auctionId + '\'' +
//                 ", bidder=" + bidderName +
//                 ", amount=" + amount +
//                 ", bidTime=" + bidTime +
//                 '}';
//     }
// }