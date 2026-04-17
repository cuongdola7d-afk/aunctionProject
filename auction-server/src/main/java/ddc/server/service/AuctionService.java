// package ddc.server.service;

// import java.time.LocalDateTime;
// import java.util.Collections;
// import java.util.List;

// import ddc.server.model.transaction.*;
// import ddc.server.model.user.*;
// import ddc.server.exception.*;

// public class AuctionService {
//         /* Đây sẽ là nơi xử lý:
//        - tạo phiên đấu giá
//        - đặt giá
//        - cập nhật người dẫn đầu
//        - kết thúc phiên đấu giá */

//     public void refreshAuctionStatus(Auction auction) {
//         if (auction == null || auction.getStartTime() == null || auction.getEndTime() == null) {
//             return;
//         }

//         if (auction.getStatus() == AuctionStatus.CANCELED) {
//             return;
//         }

//         LocalDateTime now = LocalDateTime.now();

//         if (now.isBefore(auction.getStartTime())) {
//             auction.setStatus(AuctionStatus.OPEN);
//         } else if (now.isBefore(auction.getEndTime())) {
//             auction.setStatus(AuctionStatus.RUNNING);
//         } else {
//             auction.setStatus(AuctionStatus.FINISHED);
//         }
//     }

//     public void startAuction(Auction auction) throws AuctionClosedException, InvalidBidException {
//         validateAuction(auction);
//         refreshAuctionStatus(auction);

//         if (auction.getStatus() == AuctionStatus.FINISHED) {
//             throw new AuctionClosedException("Phiên đấu giá đã kết thúc.");
//         }

//         if (LocalDateTime.now().isBefore(auction.getStartTime())) {
//             throw new AuctionClosedException("Chưa đến thời gian bắt đầu đấu giá.");
//         }

//         auction.setStatus(AuctionStatus.RUNNING);
//     }

//     public void placeBid(Auction auction, User bidder, double amount) throws AuctionClosedException, InvalidBidException {
//         // 1. Kiểm tra trạng thái (Service check sơ bộ)
//         if (auction.getStatus() != AuctionStatus.RUNNING) {
//             throw new AuctionClosedException("Phiên đấu giá đã đóng.");
//         }

//         // 2. So sánh giá (Sửa lỗi: Operator <= is undefined for double, Bid)
//         // Vì getCurrentHighestBid() trả về đối tượng Bid, bạn phải lấy .getAmount()
//         Bid highestBid = auction.getCurrentHighestBid();
//         if (highestBid != null && amount <= highestBid.getAmount()) {
//             throw new InvalidBidException("Giá bid phải lớn hơn giá hiện tại: " + highestBid.getAmount());
//         }

//         // 3. Tạo đối tượng Bid mới
//         Bid bid = new Bid(bidder, amount);

//         // 4. Gọi hàm đóng gói trong Auction để cập nhật toàn bộ thông tin
//         // Thay vì gọi addBid, setHighestBidder... vốn không có trong Auction
//         try {
//             auction.placeBid(bid); // Hàm này bạn đã viết trong class Auction rồi
//         } catch (RuntimeException e) {
//             throw new InvalidBidException(e.getMessage());
//         }

//         // 5. Cập nhật giá cho Item (nếu cần thiết)
//         auction.setCurrentPrice(amount);
//         }
    

//     public void finishAuction(Auction auction) throws InvalidBidException {
//         validateAuction(auction);
//         auction.setStatus(AuctionStatus.FINISHED);
//     }

//     public User getHighestBidder(Auction auction) {
//         if (auction == null) {
//             return null;
//         }
//         return auction.getHighestBidder();
//     }

//     public double getCurrentPrice(Auction auction) {
//         if (auction == null) {
//             return 0;
//         }
        
//         Bid highestBid = auction.getCurrentHighestBid();
        
//         // Nếu có người bid rồi thì lấy giá của họ, nếu chưa có thì trả về 0 (hoặc giá khởi điểm)
//         return (highestBid != null) ? highestBid.getAmount() : 0;
//     }

//     public List<Bid> getBidHistory(Auction auction) {
//         if (auction == null || auction.getbidHistory() == null) {
//             return Collections.emptyList();
//         }
//         return auction.getbidHistory();
//     }

//     private void validateAuction(Auction auction) throws InvalidBidException {
//         if (auction == null) {
//             throw new InvalidBidException("Auction không được null.");
//         }

//         if (auction.getItem() == null) {
//             throw new InvalidBidException("Auction chưa có item.");
//         }

//         if (auction.getStartTime() == null || auction.getEndTime() == null) {
//             throw new InvalidBidException("Auction phải có startTime và endTime.");
//         }

//         if (!auction.getEndTime().isAfter(auction.getStartTime())) {
//             throw new InvalidBidException("endTime phải sau startTime.");
//         }

//         // if (auction.getCurrentHighestBid().getAmount() == 0) {
//         //     auction.setCurrentHighestBid(auction.getCurrentPrice());
//         //     auction.getItem().setCurrentPrice(auction.getStartingPrice());
//         // }
//     }
// }
