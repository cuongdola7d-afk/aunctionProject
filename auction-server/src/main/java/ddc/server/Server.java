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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ddc.server.controller.AuctionController;
import ddc.server.model.item.Item;
import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.AuctionStatus;
import ddc.server.model.user.Bidder;
import ddc.server.network.client.AuctionEventDispatcher;
import ddc.server.network.client.ClientHandler;
import ddc.server.pattern.observer.AuctionEvent;
import ddc.server.pattern.observer.AuctionEventType;
import ddc.server.service.AuctionService;

public class Server {
    public static void main(String[] args) {
        System.out.println("Server");
    }
}
