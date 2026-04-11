package ddc.server.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import ddc.server.model.transaction.*;
import ddc.server.model.user.*;
import ddc.server.exception.*;

public class AuctionService {
        /* Đây sẽ là nơi xử lý:
       - tạo phiên đấu giá
       - đặt giá
       - cập nhật người dẫn đầu
       - kết thúc phiên đấu giá */

    public void refreshAuctionStatus(Auction auction) {
        if (auction == null || auction.getStartTime() == null || auction.getEndTime() == null) {
            return;
        }

        if (auction.getStatus() == AuctionStatus.CANCELED) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(auction.getStartTime())) {
            auction.setStatus(AuctionStatus.OPEN);
        } else if (now.isBefore(auction.getEndTime())) {
            auction.setStatus(AuctionStatus.RUNNING);
        } else {
            auction.setStatus(AuctionStatus.FINISHED);
        }
    }

    public void startAuction(Auction auction) throws AuctionClosedException, InvalidBidException {
        validateAuction(auction);
        refreshAuctionStatus(auction);

        if (auction.getStatus() == AuctionStatus.FINISHED) {
            throw new AuctionClosedException("Phiên đấu giá đã kết thúc.");
        }

        if (LocalDateTime.now().isBefore(auction.getStartTime())) {
            throw new AuctionClosedException("Chưa đến thời gian bắt đầu đấu giá.");
        }

        auction.setStatus(AuctionStatus.RUNNING);
    }

    public void placeBid(Auction auction, Bidder bidder, double amount)
            throws InvalidBidException, AuctionClosedException {

        validateAuction(auction);

        if (bidder == null) {
            throw new InvalidBidException("Người đấu giá không hợp lệ.");
        }

        refreshAuctionStatus(auction);

        if (auction.getStatus() == AuctionStatus.OPEN) {
            throw new AuctionClosedException("Phiên đấu giá chưa bắt đầu.");
        }

        if (auction.getStatus() != AuctionStatus.RUNNING) {
            throw new AuctionClosedException("Phiên đấu giá đã đóng.");
        }

        if (amount <= auction.getCurrentHighestBid()) {
            throw new InvalidBidException(
                    "Giá bid phải lớn hơn giá hiện tại: " + auction.getCurrentHighestBid()
            );
        }

        BidTransaction bidTransaction = new BidTransaction(bidder,amount,LocalDateTime.now());

        auction.addBid(bidTransaction);
        auction.setCurrentHighestBid(amount);
        auction.setHighestBidder(bidder);

        if (auction.getItem() != null) {
            auction.getItem().setCurrentPrice(amount);
        }
    }

    public void finishAuction(Auction auction) throws InvalidBidException {
        validateAuction(auction);
        auction.setStatus(AuctionStatus.FINISHED);
    }

    public Bidder getHighestBidder(Auction auction) {
        if (auction == null) {
            return null;
        }
        return auction.getHighestBidder();
    }

    public double getCurrentPrice(Auction auction) {
        if (auction == null) {
            return 0;
        }
        return auction.getCurrentHighestBid();
    }

    public List<BidTransaction> getBidHistory(Auction auction) {
        if (auction == null || auction.getBids() == null) {
            return Collections.emptyList();
        }
        return auction.getBids();
    }

    private void validateAuction(Auction auction) throws InvalidBidException {
        if (auction == null) {
            throw new InvalidBidException("Auction không được null.");
        }

        if (auction.getItem() == null) {
            throw new InvalidBidException("Auction chưa có item.");
        }

        if (auction.getStartTime() == null || auction.getEndTime() == null) {
            throw new InvalidBidException("Auction phải có startTime và endTime.");
        }

        if (!auction.getEndTime().isAfter(auction.getStartTime())) {
            throw new InvalidBidException("endTime phải sau startTime.");
        }

        if (auction.getCurrentHighestBid() == 0) {
            auction.setCurrentHighestBid(auction.getItem().getStartingPrice());
            auction.getItem().setCurrentPrice(auction.getItem().getStartingPrice());
        }
    }
}