package ddc.server.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import ddc.server.exception.AuctionClosedException;
import ddc.server.exception.InvalidBidException;
import ddc.server.model.item.Item;
import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.AuctionStatus;
import ddc.server.model.transaction.BidTransaction;
import ddc.server.model.user.Bidder;
import ddc.server.pattern.observer.AuctionEvent;
import ddc.server.pattern.observer.AuctionObserver;
import ddc.server.pattern.observer.AuctionSubject;

public class AuctionService implements AuctionSubject {
    private final List<AuctionObserver> observers = new CopyOnWriteArrayList<>();

    public Auction createAuction(Item item, LocalDateTime startTime, LocalDateTime endTime)
            throws InvalidBidException {

        if (item == null) {
            throw new InvalidBidException("Item không được null.");
        }

        if (startTime == null || endTime == null) {
            throw new InvalidBidException("Auction phải có startTime và endTime.");
        }

        if (!endTime.isAfter(startTime)) {
            throw new InvalidBidException("endTime phải sau startTime.");
        }

        Auction auction = new Auction(item, startTime, endTime);
        auction.setCurrentPrice(item.getStartingPrice());
        auction.setStatus(AuctionStatus.OPEN);
        return auction;
    }

    public void refreshAuctionStatus(Auction auction) {
        if (auction == null) {
            return;
        }

        List<AuctionEvent> pendingEvents = new ArrayList<>();

        auction.getLock().lock();
        try {
            AuctionEvent statusEvent = refreshAuctionStatusInternal(auction);
            if (statusEvent != null) {
                pendingEvents.add(statusEvent);
            }
        } finally {
            auction.getLock().unlock();
        }

        notifyAllPending(pendingEvents);
    }

    public void startAuction(Auction auction) throws AuctionClosedException, InvalidBidException {
        validateAuction(auction);

        List<AuctionEvent> pendingEvents = new ArrayList<>();

        auction.getLock().lock();
        try {
            AuctionEvent statusEvent = refreshAuctionStatusInternal(auction);
            if (statusEvent != null) {
                pendingEvents.add(statusEvent);
            }

            if (auction.getStatus() == AuctionStatus.FINISHED) {
                throw new AuctionClosedException("Phiên đấu giá đã kết thúc.");
            }

            if (auction.getStatus() == AuctionStatus.CANCELLED) {
                throw new AuctionClosedException("Phiên đấu giá đã bị hủy.");
            }

            if (auction.getStatus() == AuctionStatus.RUNNING) {
                return;
            }

            if (LocalDateTime.now().isBefore(auction.getStartTime())) {
                throw new AuctionClosedException("Chưa đến thời gian bắt đầu đấu giá.");
            }

            auction.startAuction();
            pendingEvents.add(AuctionEvent.auctionStarted(auction));
        } finally {
            auction.getLock().unlock();
        }

        notifyAllPending(pendingEvents);
    }

    public void placeBid(Auction auction, Bidder bidder, double amount)
            throws InvalidBidException, AuctionClosedException {

        validateAuction(auction);

        if (bidder == null) {
            throw new InvalidBidException("Người đấu giá không hợp lệ.");
        }

        List<AuctionEvent> pendingEvents = new ArrayList<>();

        auction.getLock().lock();
        try {
            AuctionEvent statusEvent = refreshAuctionStatusInternal(auction);
            if (statusEvent != null) {
                pendingEvents.add(statusEvent);
            }

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
                        "Giá bid phải lớn hơn giá hiện tại: " + auction.getCurrentPrice()
                );
            }

            BidTransaction bidTransaction =
                    new BidTransaction(auction.getId(), bidder, amount, LocalDateTime.now());

            auction.placeBid(bidTransaction);
            bidder.addBid(bidTransaction);

            pendingEvents.add(AuctionEvent.newBid(auction, bidTransaction));

            // Nếu muốn anti-sniping thì mở dòng này
            // auction.extendTimeIfNeeded();

        } finally {
            auction.getLock().unlock();
        }

        notifyAllPending(pendingEvents);
    }

    public void finishAuction(Auction auction) throws InvalidBidException {
        validateAuction(auction);

        List<AuctionEvent> pendingEvents = new ArrayList<>();

        auction.getLock().lock();
        try {
            if (auction.getStatus() == AuctionStatus.CANCELLED
                    || auction.getStatus() == AuctionStatus.PAID
                    || auction.getStatus() == AuctionStatus.FINISHED) {
                return;
            }

            auction.finishAuction();
            pendingEvents.add(AuctionEvent.auctionFinished(auction));
        } finally {
            auction.getLock().unlock();
        }

        notifyAllPending(pendingEvents);
    }

    public void cancelAuction(Auction auction) throws InvalidBidException {
        validateAuction(auction);

        List<AuctionEvent> pendingEvents = new ArrayList<>();

        auction.getLock().lock();
        try {
            auction.cancelAuction();
            pendingEvents.add(AuctionEvent.auctionCANCELLED(auction));
        } finally {
            auction.getLock().unlock();
        }

        notifyAllPending(pendingEvents);
    }

    public Bidder getHighestBidder(Auction auction) {
        if (auction == null) {
            return null;
        }

        auction.getLock().lock();
        try {
            return auction.getHighestBidder();
        } finally {
            auction.getLock().unlock();
        }
    }

    public double getCurrentPrice(Auction auction) {
        if (auction == null) {
            return 0;
        }

        auction.getLock().lock();
        try {
            return auction.getCurrentPrice();
        } finally {
            auction.getLock().unlock();
        }
    }

    public List<BidTransaction> getBidHistory(Auction auction) {
        if (auction == null) {
            return Collections.emptyList();
        }

        auction.getLock().lock();
        try {
            if (auction.getBids() == null) {
                return Collections.emptyList();
            }
            return new ArrayList<>(auction.getBids());
        } finally {
            auction.getLock().unlock();
        }
    }

    private AuctionEvent refreshAuctionStatusInternal(Auction auction) {
        if (auction == null || auction.getStartTime() == null || auction.getEndTime() == null) {
            return null;
        }

        if (auction.getStatus() == AuctionStatus.CANCELLED || auction.getStatus() == AuctionStatus.PAID) {
            return null;
        }

        AuctionStatus oldStatus = auction.getStatus();
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(auction.getStartTime())) {
            auction.setStatus(AuctionStatus.OPEN);
        } else if (now.isBefore(auction.getEndTime())) {
            auction.setStatus(AuctionStatus.RUNNING);
        } else {
            auction.setStatus(AuctionStatus.FINISHED);
        }

        if (oldStatus != auction.getStatus()) {
            return AuctionEvent.statusChanged(auction, oldStatus, auction.getStatus());
        }

        return null;
    }

    private void notifyAllPending(List<AuctionEvent> pendingEvents) {
        for (AuctionEvent event : pendingEvents) {
            notifyObservers(event);
        }
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

        if (auction.getCurrentPrice() <= 0) {
            auction.setCurrentPrice(auction.getItem().getStartingPrice());
        }
    }

    @Override
    public void addObserver(AuctionObserver observer) {
        if (observer != null && !observers.contains(observer)) {
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
}