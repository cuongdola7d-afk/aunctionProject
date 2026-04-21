package ddc.server.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import ddc.server.exception.AuctionClosedException;
import ddc.server.exception.InvalidBidException;
import ddc.server.model.item.ItemGeneric;
import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.AuctionStatus;
import ddc.server.model.transaction.Bid;
import ddc.server.model.user.Bidder;
import ddc.server.model.user.User;

public class AuctionService {

    public Auction createAuction(ItemGeneric item, double currentPrice, LocalDateTime startTime, LocalDateTime endTime)
            throws InvalidBidException {

        if (item == null) {
            throw new InvalidBidException("Item không được null.");
        }
        if (startTime == null) {
            throw new InvalidBidException("startTime không được null.");
        }
        if (endTime == null) {
            throw new InvalidBidException("endTime không được null.");
        }
        if (!endTime.isAfter(startTime)) {
            throw new InvalidBidException("endTime phải sau startTime.");
        }

                Auction auction = new Auction()
                            .setItem(item)
                            .setStartTime(startTime)
                            .setEndTime(endTime)
                            .setCurrentPrice(currentPrice);

        return auction;
    }

    public void refreshAuctionStatus(Auction auction) {
        if (auction == null || auction.getStartTime() == null || auction.getEndTime() == null) {
            return;
        }

        if (auction.getStatus() == AuctionStatus.CANCELLED) {
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
        validateAuctionStructure(auction);
        normalizeAuctionPriceIfNeeded(auction);
        refreshAuctionStatus(auction);

        if (auction.getStatus() == AuctionStatus.CANCELLED) {
            throw new AuctionClosedException("Phiên đấu giá đã bị hủy.");
        }

        if (auction.getStatus() == AuctionStatus.FINISHED) {
            throw new AuctionClosedException("Phiên đấu giá đã kết thúc.");
        }

        if (LocalDateTime.now().isBefore(auction.getStartTime())) {
            throw new AuctionClosedException("Chưa đến thời gian bắt đầu đấu giá.");
        }

        auction.startAuction();
    }

    public void placeBid(Auction auction, Bidder bidder, double amount, LocalDateTime time)
            throws InvalidBidException, AuctionClosedException {

        validateAuctionStructure(auction);
        normalizeAuctionPriceIfNeeded(auction);

        if (bidder == null) {
            throw new InvalidBidException("Người đấu giá không hợp lệ.");
        }

        refreshAuctionStatus(auction);

        if (auction.getStatus() == AuctionStatus.OPEN) {
            throw new AuctionClosedException("Phiên đấu giá chưa bắt đầu.");
        }

        if (auction.getStatus() == AuctionStatus.CANCELLED) {
            throw new AuctionClosedException("Phiên đấu giá đã bị hủy.");
        }

        if (auction.getStatus() != AuctionStatus.RUNNING) {
            throw new AuctionClosedException("Phiên đấu giá đã đóng.");
        }

        if (amount <= auction.getCurrentPrice()) {
            throw new InvalidBidException(
                    "Giá bid phải lớn hơn giá hiện tại: " + auction.getCurrentPrice());
        }

        Bid bid = new Bid()
                    .setBidder(bidder)
                    .setBidAmount(amount)
                    .setBidTime(time);
        bid.setAuctionId(auction.getId());

        try {
            auction.placeBid(bid);
        } catch (RuntimeException e) {
            throw new InvalidBidException(e.getMessage());
        }

        bidder.addBid(bid);
        auction.setCurrentPrice(amount);
    }

    public void finishAuction(Auction auction) throws InvalidBidException {
        validateAuctionStructure(auction);
        normalizeAuctionPriceIfNeeded(auction);

        if (auction.getStatus() == AuctionStatus.FINISHED
                || auction.getStatus() == AuctionStatus.CANCELLED) {
            return;
        }

        auction.endAuction();
    }

    public void cancelAuction(Auction auction) throws InvalidBidException, AuctionClosedException {
        validateAuctionStructure(auction);
        normalizeAuctionPriceIfNeeded(auction);

        if (auction.getStatus() == AuctionStatus.FINISHED) {
            throw new AuctionClosedException("Phiên đấu giá đã kết thúc, không thể hủy!");
        }

        if (auction.getStatus() == AuctionStatus.CANCELLED) {
            return;
        }

        auction.setStatus(AuctionStatus.CANCELLED);
    }

    public User getHighestBidder(Auction auction) {
        if (auction == null) {
            return null;
        }
        return auction.getHighestBidder();
    }

    public double getCurrentPrice(Auction auction) {
        if (auction == null) {
            return 0;
        }
        return auction.getCurrentPrice();
    }

    public List<Bid> getBidHistory(Auction auction) {
        if (auction == null || auction.getBidHistory() == null) {
            return Collections.emptyList();
        }
        return auction.getBidHistory();
    }

    private void validateAuctionStructure(Auction auction) throws InvalidBidException {
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
    }

    private void normalizeAuctionPriceIfNeeded(Auction auction) {
        if (auction != null
                && auction.getItem() != null
                && auction.getCurrentPrice() <= 0) {
            auction.setCurrentPrice(auction.getCurrentPrice());
        }
    }
}