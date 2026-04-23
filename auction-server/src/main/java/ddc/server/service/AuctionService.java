package ddc.server.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ddc.server.exception.AuctionClosedException;
import ddc.server.exception.InvalidBidException;
import ddc.server.model.item.ItemGeneric;
import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.AuctionStatus;
import ddc.server.model.transaction.Bid;
import ddc.server.model.user.Bidder;
import ddc.server.model.user.User;
import ddc.server.pattern.observer.AuctionEvent;
import ddc.server.pattern.observer.AuctionObserver;
import ddc.server.pattern.observer.AuctionSubject;

public class AuctionService implements AuctionSubject {

    private final List<AuctionObserver> observers = new CopyOnWriteArrayList<>();

    @Override
    public void addObserver(AuctionObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(AuctionObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(AuctionEvent event) {
        for (AuctionObserver observer : observers) {
            observer.update(event);
        }
    }

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
        if (currentPrice < 0) {
            throw new InvalidBidException("currentPrice không được âm.");
        }

        return new Auction()
                .setItem(item)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setCurrentPrice(currentPrice)
                .setStatus(AuctionStatus.OPEN);
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

        AuctionStatus oldStatus = auction.getStatus();
        refreshAuctionStatus(auction);
        AuctionStatus newStatus = auction.getStatus();

        if (newStatus == AuctionStatus.CANCELLED) {
            throw new AuctionClosedException("Phiên đấu giá đã bị hủy.");
        }

        if (newStatus == AuctionStatus.FINISHED) {
            if (oldStatus != newStatus) {
                emitTransitionEvents(auction, oldStatus, newStatus);
            }
            throw new AuctionClosedException("Phiên đấu giá đã kết thúc.");
        }

        if (newStatus == AuctionStatus.OPEN) {
            throw new AuctionClosedException("Chưa đến thời gian bắt đầu đấu giá.");
        }

        if (oldStatus != newStatus) {
            emitTransitionEvents(auction, oldStatus, newStatus);
        }
    }

    public void placeBid(Auction auction, Bidder bidder, double amount, LocalDateTime time)
            throws InvalidBidException, AuctionClosedException {

        validateAuctionStructure(auction);
        normalizeAuctionPriceIfNeeded(auction);

        if (bidder == null) {
            throw new InvalidBidException("Người đấu giá không hợp lệ.");
        }

        if (amount <= 0) {
            throw new InvalidBidException("Giá bid phải lớn hơn 0.");
        }

        if (time == null) {
            time = LocalDateTime.now();
        }

        AuctionStatus oldStatus = auction.getStatus();
        refreshAuctionStatus(auction);
        AuctionStatus newStatus = auction.getStatus();

        if (oldStatus != newStatus) {
            emitTransitionEvents(auction, oldStatus, newStatus);
        }

        if (newStatus == AuctionStatus.OPEN) {
            throw new AuctionClosedException("Phiên đấu giá chưa bắt đầu.");
        }

        if (newStatus == AuctionStatus.CANCELLED) {
            throw new AuctionClosedException("Phiên đấu giá đã bị hủy.");
        }

        if (newStatus != AuctionStatus.RUNNING) {
            throw new AuctionClosedException("Phiên đấu giá đã đóng.");
        }

        if (amount <= auction.getCurrentPrice()) {
            throw new InvalidBidException(
                    "Giá bid phải lớn hơn giá hiện tại: " + auction.getCurrentPrice());
        }

        Bid bid = new Bid()
                .setAuctionId(auction.getId())
                .setBidder(bidder)
                .setBidAmount(amount)
                .setBidTime(time);

        try {
            auction.placeBid(bid);
        } catch (RuntimeException e) {
            throw new InvalidBidException(e.getMessage());
        }

        bidder.addBid(bid);

        notifyObservers(AuctionEvent.newBid(auction, bid));
    }

    public void finishAuction(Auction auction) throws InvalidBidException {
        validateAuctionStructure(auction);
        normalizeAuctionPriceIfNeeded(auction);

        AuctionStatus oldStatus = auction.getStatus();

        if (oldStatus == AuctionStatus.FINISHED || oldStatus == AuctionStatus.CANCELLED) {
            return;
        }

        auction.endAuction();

        emitTransitionEvents(auction, oldStatus, auction.getStatus());
    }

    public void cancelAuction(Auction auction) throws InvalidBidException, AuctionClosedException {
        validateAuctionStructure(auction);
        normalizeAuctionPriceIfNeeded(auction);

        AuctionStatus oldStatus = auction.getStatus();

        if (oldStatus == AuctionStatus.FINISHED) {
            throw new AuctionClosedException("Phiên đấu giá đã kết thúc, không thể hủy.");
        }

        if (oldStatus == AuctionStatus.CANCELLED) {
            return;
        }

        auction.setStatus(AuctionStatus.CANCELLED);

        emitTransitionEvents(auction, oldStatus, auction.getStatus());
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
        return Collections.unmodifiableList(auction.getBidHistory());
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
        if (auction != null && auction.getCurrentPrice() < 0) {
            auction.setCurrentPrice(0);
        }
    }

    private void emitTransitionEvents(Auction auction, AuctionStatus oldStatus, AuctionStatus newStatus) {
        if (oldStatus == newStatus) {
            return;
        }

        notifyObservers(AuctionEvent.statusChanged(auction, oldStatus, newStatus));

        if (newStatus == AuctionStatus.RUNNING) {
            notifyObservers(AuctionEvent.auctionStarted(auction));
        } else if (newStatus == AuctionStatus.FINISHED) {
            notifyObservers(AuctionEvent.auctionFinished(auction));
        } else if (newStatus == AuctionStatus.CANCELLED) {
            notifyObservers(AuctionEvent.auctionCANCELLED(auction));
        }
    }
}