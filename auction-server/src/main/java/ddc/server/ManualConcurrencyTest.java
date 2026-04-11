package ddc.server;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ddc.server.model.item.Item;
import ddc.server.model.transaction.Auction;
import ddc.server.model.user.Bidder;
import ddc.server.pattern.observer.LoggingAuctionObserver;
import ddc.server.service.AuctionService;

public class ManualConcurrencyTest {

    static class DemoItem extends Item {
        public DemoItem(String name, String description, double startingPrice) {
            super(name, description, startingPrice);
            setCurrentPrice(startingPrice);
        }

        @Override
        public String getCategory() {
            return "TEST_ITEM";
        }
    }

    public static void main(String[] args) {
        try {
            AuctionService service = new AuctionService();
            service.addObserver(new LoggingAuctionObserver());

            Item item = new DemoItem("Ban phim co", "Test concurrency", 100000);

            Auction auction = service.createAuction(
                    item,
                    LocalDateTime.now().minusMinutes(1),
                    LocalDateTime.now().plusMinutes(10)
            );

            Bidder alice = new Bidder();
            alice.setName("Alice");

            Bidder bob = new Bidder();
            bob.setName("Bob");

            Bidder charlie = new Bidder();
            charlie.setName("Charlie");

            ExecutorService executor = Executors.newFixedThreadPool(3);
            CountDownLatch ready = new CountDownLatch(3);
            CountDownLatch start = new CountDownLatch(1);
            CountDownLatch done = new CountDownLatch(3);

            Runnable bid1 = createBidTask(service, auction, alice, 120000, ready, start, done);
            Runnable bid2 = createBidTask(service, auction, bob, 150000, ready, start, done);
            Runnable bid3 = createBidTask(service, auction, charlie, 180000, ready, start, done);

            executor.submit(bid1);
            executor.submit(bid2);
            executor.submit(bid3);

            ready.await();
            System.out.println("All bidders ready. Start concurrent bidding...");
            start.countDown();

            done.await();
            executor.shutdown();

            System.out.println("===== KET QUA CUOI =====");
            System.out.println("Current price: " + service.getCurrentPrice(auction));
            System.out.println("Highest bidder: " +
                    (service.getHighestBidder(auction) != null
                            ? service.getHighestBidder(auction).getName()
                            : "null"));
            System.out.println("Bid history size: " + service.getBidHistory(auction).size());

            System.out.println("===== CHI TIET BID =====");
            service.getBidHistory(auction).forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Runnable createBidTask(
            AuctionService service,
            Auction auction,
            Bidder bidder,
            double amount,
            CountDownLatch ready,
            CountDownLatch start,
            CountDownLatch done
    ) {
        return () -> {
            try {
                ready.countDown();
                start.await();

                service.placeBid(auction, bidder, amount);
                System.out.println(bidder.getName() + " bid SUCCESS: " + amount);

            } catch (Exception e) {
                System.out.println(bidder.getName() + " bid FAILED: " + amount + " | " + e.getMessage());
            } finally {
                done.countDown();
            }
        };
    }
}