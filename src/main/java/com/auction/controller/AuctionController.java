package com.auction.controller;

import java.util.List;

import com.auction.entity.auction.Auction;
import com.auction.entity.auction.AuctionStatus;
import com.auction.entity.auction.BidTransaction;
import com.auction.entity.user.Bidder;
import com.auction.exception.AuctionClosedException;
import com.auction.exception.InvalidBidException;
import com.auction.service.AuctionService;

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