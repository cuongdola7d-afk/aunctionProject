package ddc.server.network.client;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import ddc.server.controller.AuctionController;
import ddc.server.exception.AuctionClosedException;
import ddc.server.exception.AuctionNotFoundException;
import ddc.server.exception.BidderNotFoundException;
import ddc.server.exception.InvalidBidException;
import ddc.server.model.transaction.Auction;
import ddc.server.model.user.Bidder;
import ddc.server.network.message.MessageType;
import ddc.server.network.message.SocketMessage;
import ddc.server.network.request.PlaceBidRequest;
import ddc.server.network.request.SubscribeAuctionRequest;
import ddc.server.network.response.AuctionEventResponse;
import ddc.server.network.response.ErrorResponse;
import ddc.server.pattern.Singleton.AuctionManager;

public class ClientHandler implements Runnable {
    private final ClientConnection connection;
    private final AuctionController auctionController;
    private final AuctionEventDispatcher dispatcher;
    private final Gson gson = new Gson();

    public ClientHandler(
            Socket socket,
            BufferedReader reader,
            PrintWriter writer,
            AuctionController auctionController,
            AuctionEventDispatcher dispatcher
    ) {
        this.connection = new ClientConnection(socket, reader, writer);
        this.auctionController = auctionController;
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        try {
            String line;

            while ((line = connection.getReader().readLine()) != null) {
                handleRawMessage(line);
            }
        } catch (Exception e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            dispatcher.unsubscribeAll(connection);
            connection.close();
        }
    }

    private void handleRawMessage(String line) {
        try {
            SocketMessage message = gson.fromJson(line, SocketMessage.class);

            if (message == null || message.getType() == null) {
                sendError("Invalid message.");
                return;
            }

            switch (message.getType()) {
                case SUBSCRIBE_AUCTION -> handleSubscribe(message.getPayloadJson());
                case PLACE_BID -> handlePlaceBid(message.getPayloadJson());
                default -> sendError("Unsupported message type: " + message.getType());
            }
        } catch (Exception e) {
            sendError("Failed to parse message: " + e.getMessage());
        }
    }

    private void handleSubscribe(String payloadJson) {
        try {
            SubscribeAuctionRequest request = gson.fromJson(payloadJson, SubscribeAuctionRequest.class);
            if (request == null || request.getAuctionId() == null) {
                sendError("auctionId is required.");
                return;
            }

            Auction auction = AuctionManager.getInstance().getAuctionOrThrow(request.getAuctionId());

            auctionController.handleRefreshStatus(auction);
            dispatcher.subscribe(request.getAuctionId(), connection);

            AuctionEventResponse snapshot = AuctionEventResponse.fromAuctionState(auction);
            connection.send(MessageType.AUCTION_EVENT, snapshot, gson);

        } catch (AuctionNotFoundException e) {
            sendError(e.getMessage());
        } catch (Exception e) {
            sendError("Không thể subscribe auction: " + e.getMessage());
        }
    }

    private void handlePlaceBid(String payloadJson) {
        try {
            PlaceBidRequest request = gson.fromJson(payloadJson, PlaceBidRequest.class);
            if (request == null) {
                sendError("Invalid place bid request.");
                return;
            }

            Auction auction = AuctionManager.getInstance().getAuctionOrThrow(request.getAuctionId());
            Bidder bidder = AuctionManager.getInstance().getBidderOrThrow(request.getBidderId());

            auctionController.handlePlaceBid(auction, bidder, request.getAmount());

        } catch (AuctionNotFoundException | BidderNotFoundException e) {
            sendError(e.getMessage());
        } catch (InvalidBidException | AuctionClosedException e) {
            sendError(e.getMessage());
        } catch (Exception e) {
            sendError("Đặt giá thất bại: " + e.getMessage());
        }
    }

    private void sendError(String message) {
        connection.send(MessageType.ERROR, new ErrorResponse(message), gson);
    }
}