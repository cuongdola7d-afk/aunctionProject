package com.aunction.controller;

import com.aunction.entity.auction.Auction;
import com.aunction.entity.auction.AuctionStatus;
import com.aunction.entity.auction.BidTransaction;
import com.aunction.entity.user.Bidder;
import com.aunction.exception.AuctionClosedException;
import com.aunction.exception.InvalidBidException;
import com.aunction.service.AuctionService;

import java.util.List;

public class AuctionController {
    private final AuctionService auctionService;

    public AuctionController() {
        this.auctionService = new AuctionService();
    }

    public void handleStartAuction(Auction auction) throws AuctionClosedException, InvalidBidException {
        auctionService.startAuction(auction);
    }

    public void handlePlaceBid(Auction auction, Bidder bidder, double amount)
            throws InvalidBidException, AuctionClosedException {
        auctionService.placeBid(auction, bidder, amount);
    }

    public void handleFinishAuction(Auction auction) throws InvalidBidException {
        auctionService.finishAuction(auction);
    }

    public AuctionStatus handleRefreshStatus(Auction auction) {
        auctionService.refreshAuctionStatus(auction);
        return auction.getStatus();
    }

    public double getCurrentPrice(Auction auction) {
        return auctionService.getCurrentPrice(auction);
    }

    public Bidder getHighestBidder(Auction auction) {
        return auctionService.getHighestBidder(auction);
    }

    public List<BidTransaction> getBidHistory(Auction auction) {
        return auctionService.getBidHistory(auction);
    }
}