package ddc.server.network.client;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;

import ddc.server.network.message.MessageType;
import ddc.server.network.response.AuctionEventResponse;
import ddc.server.pattern.observer.AuctionEvent;
import ddc.server.pattern.observer.AuctionObserver;

public class AuctionEventDispatcher implements AuctionObserver {
    private final ConcurrentHashMap<String, Set<ClientConnection>> subscribers = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void subscribe(String auctionId, ClientConnection connection) {
        subscribers
                .computeIfAbsent(auctionId, key -> ConcurrentHashMap.newKeySet())
                .add(connection);

        connection.subscribe(auctionId);
    }

    public void unsubscribeAll(ClientConnection connection) {
        for (Set<ClientConnection> connections : subscribers.values()) {
            connections.remove(connection);
        }
        connection.unsubscribeAll();
    }

    @Override
    public void update(AuctionEvent event) {
        if (event == null || event.getAuctionId() == null) {
            return;
        }

        Set<ClientConnection> connections = subscribers.get(event.getAuctionId());
        if (connections == null || connections.isEmpty()) {
            return;
        }

        AuctionEventResponse response = AuctionEventResponse.fromAuctionEvent(event);

        for (ClientConnection connection : connections) {
            try {
                connection.send(MessageType.AUCTION_EVENT, response, gson);
            } catch (Exception e) {
                unsubscribeAll(connection);
                connection.close();
            }
        }
    }

    public void dispatch(AuctionEvent event) {
        String auctionId = event.getAuctionId();
        
        // Lấy danh sách những người đang xem phiên này
        Set<ClientConnection> clients = subscribers.get(auctionId);
        
        if (clients != null) {
            // Duyệt qua từng kết nối
            for (ClientConnection connection : clients) {
                // Gọi đúng hàm send(type, payload, gson) trong ảnh của bạn
                connection.send(
                    MessageType.AUCTION_EVENT, // Loại tin nhắn (bạn kiểm tra xem trong MessageType có cái này chưa nhé)
                    event,                     // Dữ liệu sự kiện
                    this.gson                  // Truyền đối tượng gson của Dispatcher vào
                );
            }
        }
    }
}