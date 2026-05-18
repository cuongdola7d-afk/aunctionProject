package ddc.server.model.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import ddc.server.model.user.User;

class AuctionConcurrencyTest {

    @Test
    void placeBid_concurrentCallers_shouldKeepInvariants() throws InterruptedException {
        int threads = 32;
        int bidsPerThread = 50;

        Auction auction = new Auction()
                .setId("A-CC")
                .setStartingPrice(100)
                .setCurrentPrice(100);
        auction.startAuction();

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);
        AtomicInteger accepted = new AtomicInteger();
        AtomicInteger rejected = new AtomicInteger();

        for (int t = 0; t < threads; t++) {
            final int threadIdx = t;
            pool.submit(() -> {
                try {
                    start.await();
                    for (int i = 0; i < bidsPerThread; i++) {
                        User bidder = new User().setName("u-" + threadIdx + "-" + i);
                        bidder.setId("u-" + threadIdx + "-" + i);
                        double amount;
                        synchronized (auction) {
                            amount = auction.getCurrentPrice() + 1;
                        }
                        Bid bid = new Bid()
                                .setAuctionId("A-CC")
                                .setBidder(bidder)
                                .setBidAmount(amount)
                                .setBidTime(LocalDateTime.now());
                        try {
                            auction.placeBid(bid);
                            accepted.incrementAndGet();
                        } catch (RuntimeException ex) {
                            rejected.incrementAndGet();
                        }
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        assertTrue(done.await(20, TimeUnit.SECONDS), "Threads did not finish in time");
        pool.shutdownNow();

        int total = threads * bidsPerThread;
        assertEquals(total, accepted.get() + rejected.get(), "Tổng số attempt phải bằng accepted + rejected");
        assertEquals(accepted.get(), auction.getBidHistory().size(),
                "BidHistory size phải bằng số bid thành công");
        assertTrue(auction.getCurrentPrice() >= 100, "currentPrice không được tụt dưới startingPrice");

        double prev = -1;
        for (Bid b : auction.getBidHistory()) {
            assertTrue(b.getBidAmount() > prev,
                    "BidHistory phải đơn điệu tăng, gặp " + b.getBidAmount() + " sau " + prev);
            prev = b.getBidAmount();
        }
    }
}

