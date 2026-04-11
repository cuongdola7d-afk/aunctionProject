package ddc.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ddc.server.controller.AuctionController;
import ddc.server.model.item.Item;
import ddc.server.model.transaction.Auction;
import ddc.server.model.user.Bidder;
import ddc.server.network.AuctionEventDispatcher;
import ddc.server.network.ClientHandler;
import ddc.server.service.AuctionService;

public class Server {
    private static final int PORT = 5555;

    // ID cố định để test
    public static final String DEMO_AUCTION_ID = "AUCT-001";
    public static final String BIDDER_ALICE_ID = "BIDDER-ALICE";
    public static final String BIDDER_BOB_ID = "BIDDER-BOB";
    public static final String BIDDER_CHARLIE_ID = "BIDDER-CHARLIE";

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<String, Auction> auctionStore = new ConcurrentHashMap<>();
    private final Map<String, Bidder> bidderStore = new ConcurrentHashMap<>();

    private final AuctionService auctionService;
    private final AuctionController auctionController;
    private final AuctionEventDispatcher dispatcher;

    public Server() throws Exception {
        this.auctionService = new AuctionService();
        this.auctionController = new AuctionController(auctionService);
        this.dispatcher = new AuctionEventDispatcher();

        auctionService.addObserver(dispatcher);

        seedDemoData();
    }

    public void start() throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Auction server running on port " + PORT);
            printDemoIds();

            while (true) {
                Socket socket = serverSocket.accept();

                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                ClientHandler handler = new ClientHandler(
                        socket,
                        reader,
                        writer,
                        auctionController,
                        dispatcher,
                        auctionStore,
                        bidderStore
                );

                executor.submit(handler);
            }
        }
    }

    private void seedDemoData() throws Exception {
        Item item = new DemoItem("Ban phim co", "Demo keyboard", 100000);

        Auction auction = auctionService.createAuction(
                item,
                LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().plusMinutes(30)
        );
        auction.setId(DEMO_AUCTION_ID);

        Bidder alice = new Bidder();
        alice.setId(BIDDER_ALICE_ID);
        alice.setName("Alice");

        Bidder bob = new Bidder();
        bob.setId(BIDDER_BOB_ID);
        bob.setName("Bob");

        Bidder charlie = new Bidder();
        charlie.setId(BIDDER_CHARLIE_ID);
        charlie.setName("Charlie");

        auctionStore.clear();
        bidderStore.clear();

        auctionStore.put(auction.getId(), auction);
        bidderStore.put(alice.getId(), alice);
        bidderStore.put(bob.getId(), bob);
        bidderStore.put(charlie.getId(), charlie);
    }

    private void printDemoIds() {
        System.out.println("=== DEMO AUCTIONS ===");
        System.out.println("auctionId = " + DEMO_AUCTION_ID + " | item = Ban phim co");

        System.out.println("=== DEMO BIDDERS ===");
        System.out.println("bidderId = " + BIDDER_ALICE_ID + " | name = Alice");
        System.out.println("bidderId = " + BIDDER_BOB_ID + " | name = Bob");
        System.out.println("bidderId = " + BIDDER_CHARLIE_ID + " | name = Charlie");
    }

    public static void main(String[] args) throws Exception {
        new Server().start();
    }

    private static class DemoItem extends Item {
        public DemoItem(String name, String description, double startingPrice) {
            super(name, description, startingPrice);
            setCurrentPrice(startingPrice);
        }

        @Override
        public String getCategory() {
            return "DEMO";
        }
    }
}