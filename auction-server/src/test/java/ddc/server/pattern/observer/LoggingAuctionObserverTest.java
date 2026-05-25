package ddc.server.pattern.observer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import ddc.server.config.GsonConfig;
import ddc.server.model.transaction.AuctionStatus;
import ddc.server.network.client.ClientConnection;
import ddc.server.network.message.MessageType;

class LoggingAuctionObserverTest {

    private final Gson gson = GsonConfig.newGson();

    @Test
    void update_shouldIgnoreNullEventBlankAuctionAndEmptyConnections() {
        ClientConnection connection = mock(ClientConnection.class);
        LoggingAuctionObserver observer = new LoggingAuctionObserver(Set.of(connection), gson);

        observer.update(null);
        verify(connection, never()).send(eq(MessageType.AUCTION_EVENT), org.mockito.ArgumentMatchers.any(), eq(gson));

        observer.update(event(AuctionEventType.NEW_BID, null));
        observer.update(event(AuctionEventType.NEW_BID, "   "));
        verify(connection, never()).send(eq(MessageType.AUCTION_EVENT), org.mockito.ArgumentMatchers.any(), eq(gson));

        new LoggingAuctionObserver(Set.of(), gson).update(event(AuctionEventType.NEW_BID, "A001"));
    }

    @Test
    void update_shouldSendPayloadToSubscribedConnectionsOnly() {
        ClientConnection subscribed = mock(ClientConnection.class);
        ClientConnection notSubscribed = mock(ClientConnection.class);
        when(subscribed.isSubscribedTo("A001")).thenReturn(true);
        when(notSubscribed.isSubscribedTo("A001")).thenReturn(false);

        Set<ClientConnection> connections = new LinkedHashSet<>();
        connections.add(null);
        connections.add(notSubscribed);
        connections.add(subscribed);
        LoggingAuctionObserver observer = new LoggingAuctionObserver(connections, gson);

        observer.update(event(AuctionEventType.NEW_BID, "A001"));

        verify(notSubscribed, never()).send(eq(MessageType.AUCTION_EVENT), org.mockito.ArgumentMatchers.any(), eq(gson));
        ArgumentCaptor<JsonObject> payload = ArgumentCaptor.forClass(JsonObject.class);
        verify(subscribed).send(eq(MessageType.AUCTION_EVENT), payload.capture(), eq(gson));
        assertEquals("NEW_BID", payload.getValue().get("eventType").getAsString());
        assertEquals("A001", payload.getValue().get("auctionId").getAsString());
        assertEquals("I001", payload.getValue().get("itemId").getAsString());
        assertEquals("Camera", payload.getValue().get("itemName").getAsString());
        assertEquals("buyer", payload.getValue().get("bidderName").getAsString());
        assertEquals(120, payload.getValue().get("bidAmount").getAsDouble());
        assertEquals(150, payload.getValue().get("currentPrice").getAsDouble());
        assertEquals("RUNNING", payload.getValue().get("status").getAsString());
        assertEquals("2026-05-25 10:15:30", payload.getValue().get("eventTime").getAsString());
        assertEquals("message", payload.getValue().get("message").getAsString());
    }

    @Test
    void update_shouldMapAllEventTypesAndCatchSendFailure() {
        ClientConnection connection = mock(ClientConnection.class);
        when(connection.isSubscribedTo("A001")).thenReturn(true);
        when(connection.getConnectionId()).thenReturn("C001");
        LoggingAuctionObserver observer = new LoggingAuctionObserver(Set.of(connection), gson);

        assertSentEventType(observer, connection, AuctionEventType.AUCTION_STARTED, "AUCTION_STARTED");
        assertSentEventType(observer, connection, AuctionEventType.AUCTION_FINISHED, "AUCTION_FINISHED");
        assertSentEventType(observer, connection, AuctionEventType.AUCTION_CANCELLED, "AUCTION_CANCELLED");
        assertSentEventType(observer, connection, AuctionEventType.STATUS_CHANGED, "STATUS_CHANGED");
        assertSentEventType(observer, connection, null, "UNKNOWN");

        doThrow(new RuntimeException("send failed"))
                .when(connection).send(eq(MessageType.AUCTION_EVENT), org.mockito.ArgumentMatchers.any(), eq(gson));
        observer.update(event(AuctionEventType.NEW_BID, "A001"));
        verify(connection).getConnectionId();
    }

    private void assertSentEventType(
            LoggingAuctionObserver observer,
            ClientConnection connection,
            AuctionEventType sourceType,
            String expectedType
    ) {
        reset(connection);
        when(connection.isSubscribedTo("A001")).thenReturn(true);
        ArgumentCaptor<JsonObject> payload = ArgumentCaptor.forClass(JsonObject.class);

        observer.update(event(sourceType, "A001"));

        verify(connection).send(eq(MessageType.AUCTION_EVENT), payload.capture(), eq(gson));
        assertEquals(expectedType, payload.getValue().get("eventType").getAsString());
    }

    private AuctionEvent event(AuctionEventType type, String auctionId) {
        return new AuctionEvent(
                type,
                auctionId,
                "I001",
                "Camera",
                "buyer",
                120,
                150,
                AuctionStatus.RUNNING,
                LocalDateTime.of(2026, 5, 25, 10, 15, 30),
                "message"
        );
    }
}
