package com.auction.controller;

import java.time.LocalDateTime;

import com.auction.entity.auction.Auction;
import com.auction.entity.item.Electronics;
import com.auction.entity.user.Bidder;
import com.auction.entity.user.Seller;
import com.auction.exception.AuctionClosedException;
import com.auction.exception.InvalidBidException;

public class Main {
    public static void main(String[] args) {
        try {
            Seller seller = new Seller();
            seller.setName("Nguyen Van A");
            seller.setEmail("seller@gmail.com");
            seller.setPassword("123456");

            Electronics laptop = new Electronics();
            laptop.setName("Laptop Dell XPS");
            laptop.setDescription("Core i7, 16GB RAM");
            laptop.setStartingPrice(1000.0);
            laptop.setCurrentPrice(1000.0);
            laptop.setSeller(seller);
            laptop.setBrand("Dell");
            laptop.setWarrantyMonths(24);

            Auction auction = new Auction();
            auction.setItem(laptop);
            auction.setStartTime(LocalDateTime.now().minusMinutes(1));
            auction.setEndTime(LocalDateTime.now().plusMinutes(30));
            auction.setCurrentHighestBid(laptop.getStartingPrice());

            Bidder bidder1 = new Bidder();
            bidder1.setName("Bidder 1");
            bidder1.setEmail("bidder1@gmail.com");
            bidder1.setPassword("111111");

            Bidder bidder2 = new Bidder();
            bidder2.setName("Bidder 2");
            bidder2.setEmail("bidder2@gmail.com");
            bidder2.setPassword("222222");

            AuctionController controller = new AuctionController();

            controller.handleRefreshStatus(auction);

            controller.handlePlaceBid(auction, bidder1, 1100.0);
            controller.handlePlaceBid(auction, bidder2, 1250.0);

            System.out.println("Trang thai auction: " + auction.getStatus());
            System.out.println("Gia hien tai: " + controller.getCurrentPrice(auction));
            System.out.println("Nguoi dang dan dau: " + controller.getHighestBidder(auction).getName());
            System.out.println("So luot bid: " + controller.getBidHistory(auction).size());

            System.out.println("\nDanh sach lich su bid:");
            for (var bid : controller.getBidHistory(auction)) {
                System.out.println(bid);
            }

        } catch (InvalidBidException | AuctionClosedException e) {
            System.out.println("Loi: " + e.getMessage());
        }
    }
}