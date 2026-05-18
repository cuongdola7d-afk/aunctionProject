package ddc.server.controller.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import ddc.server.dao.AuctionDAO;
import ddc.server.exception.AuctionClosedException;
import ddc.server.exception.InvalidBidException;
import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.AuctionStatus;
import ddc.server.model.transaction.Bid;
import ddc.server.model.user.Bidder;
import ddc.server.model.user.User;

public class AuctionService {
    private final AuctionDAO auctionDAO;

    public AuctionService() {
        this.auctionDAO = new AuctionDAO();
    }

    public boolean createAuction(Auction auction) {
        boolean isSuccess = auctionDAO.createAuction(auction);

        return isSuccess;
    }

    public List<Auction> getAll() {
        return auctionDAO.getAllAuctions();
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
            auction.setStatus("OPEN");
        } else if (now.isBefore(auction.getEndTime())) {
            auction.setStatus("RUNNING");
        } else {
            auction.setStatus("FINISHED");
        }
    }

    public void startAuction(Auction auction) throws AuctionClosedException, InvalidBidException {
        validateAuctionStructure(auction);
        normalizeAuctionPriceIfNeeded(auction);
        refreshAuctionStatus(auction);

        if (auction.getStatus() == AuctionStatus.FINISHED) {
            throw new AuctionClosedException("Phiên đấu giá đã kết thúc.");
        }

        if (auction.getStatus() == AuctionStatus.CANCELLED) {
            throw new AuctionClosedException("Phiên đấu giá đã bị hủy.");
        }

        if (LocalDateTime.now().isBefore(auction.getStartTime())) {
            throw new AuctionClosedException("Chưa đến thời gian bắt đầu đấu giá.");
        }

        auction.startAuction();
    }

    /**
     * @return true nếu endTime bị gia hạn bởi anti-snip
     */
    public boolean placeBid(Auction auction, Bidder bidder, double amount, LocalDateTime time)
            throws AuctionClosedException, InvalidBidException {

        validateAuctionStructure(auction);
        normalizeAuctionPriceIfNeeded(auction);

        if (bidder == null) {
            throw new InvalidBidException("Người đấu giá không hợp lệ.");
        }

        synchronized (auction) {
            return placeBidLocked(auction, bidder, amount, time);
        }
    }

    private boolean placeBidLocked(Auction auction, Bidder bidder, double amount, LocalDateTime time)
            throws AuctionClosedException, InvalidBidException {
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

        // Chặn bidder đặt giá liên tục khi đang giữ giá cao nhất
        User currentHighestUser = auction.getHighestBidder();
        if (currentHighestUser != null && currentHighestUser.getId().equals(bidder.getId())) {
            throw new InvalidBidException("Bạn đang giữ giá cao nhất, chờ người khác đặt giá.");
        }

        // Kiểm tra giá phải là số nguyên
        if (amount != Math.floor(amount)) {
            throw new InvalidBidException("Giá đặt phải là số nguyên, không chấp nhận số thập phân.");
        }

        // Kiểm tra bước giá tối thiểu = 10% giá gốc
        long minIncrement = auction.getMinBidIncrement();
        double minBid = auction.getCurrentPrice() + minIncrement;
        if (amount < minBid) {
            throw new InvalidBidException(
                    "Giá tối thiểu phải là " + String.format("%,.0f", minBid)
                    + " (bước giá: " + String.format("%,.0f", (double) minIncrement) + ")");
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

        // Anti-snipping: gia hạn nếu bid trong khoảng cuối
        return applyAntiSnipExtension(auction, time);
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

        auction.setStatus("CANCELLED");
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
        // Đảm bảo startingPrice luôn có giá trị
        if (auction != null && auction.getStartingPrice() <= 0 && auction.getCurrentPrice() > 0) {
            auction.setStartingPrice(auction.getCurrentPrice());
        }
    }

    /**
     * Kiểm tra và gia hạn endTime nếu bid nằm trong khoảng anti-snip.
     * 
     * @return true nếu endTime đã được gia hạn
     */

    public boolean applyAntiSnipExtension(Auction auction, LocalDateTime bidTime) {
        if (auction.getEndTime() == null || bidTime == null) {
            return false;
        }

        long secondsRemaining = java.time.Duration.between(bidTime, auction.getEndTime()).getSeconds();

        if (secondsRemaining > 0 && secondsRemaining <= auction.getAntiSnipThresholdSeconds()) {
            LocalDateTime newEndTime = auction.getEndTime().plusSeconds(auction.getAntiSnipExtensionSeconds());
            auction.setEndTime(newEndTime);
            return true;
        }
        return false;
    }
}
