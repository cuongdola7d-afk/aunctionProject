package ddc.server.network;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import ddc.server.model.notification.Notification;
import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.Bid;
import ddc.server.model.user.User;
import ddc.server.network.message.MessageType;
import ddc.server.network.message.SocketMessage;
import ddc.server.network.request.AuctionEventPayload;
import ddc.server.network.request.DashboardUpdatePayload;
import ddc.server.network.request.DepositRequest;
import ddc.server.network.request.PlaceBidRequest;
import ddc.server.network.request.SubscribeAuctionRequest;
import ddc.server.network.request.WalletRequest;
import ddc.server.network.response.AddItemResponse;
import ddc.server.network.response.AdminStatsResponse;
import ddc.server.network.response.BaseResponse;
import ddc.server.network.response.DepositResponse;
import ddc.server.network.response.ErrorPayload;
import ddc.server.network.response.GetAllAuctionsResponse;
import ddc.server.network.response.GetAllUserBidResponse;
import ddc.server.network.response.GetItemResponse;
import ddc.server.network.response.NotificationResponse;
import ddc.server.network.response.Response;
import ddc.server.network.response.UserListResponse;
import ddc.server.network.response.UserResponse;

class NetworkDtoTest {

    @Test
    void requestDtos_shouldRoundTripValues() {
        AuctionEventPayload event = new AuctionEventPayload();
        event.setEventType("NEW_BID");
        event.setAuctionId("A001");
        event.setCurrentPrice(120);
        event.setStatus("RUNNING");
        event.setBidderName("buyer");
        event.setBidAmount(125);
        event.setStartTime("2026-05-25T10:00");
        event.setEndTime("2026-05-25T11:00");
        event.setMessage("ok");
        event.setTimeExtended(true);
        event.setMinBidIncrement(5);

        assertEquals("NEW_BID", event.getEventType());
        assertEquals("A001", event.getAuctionId());
        assertEquals(120, event.getCurrentPrice());
        assertEquals("RUNNING", event.getStatus());
        assertEquals("buyer", event.getBidderName());
        assertEquals(125, event.getBidAmount());
        assertEquals("2026-05-25T10:00", event.getStartTime());
        assertEquals("2026-05-25T11:00", event.getEndTime());
        assertEquals("ok", event.getMessage());
        assertTrue(event.isTimeExtended());
        assertEquals(5, event.getMinBidIncrement());

        DashboardUpdatePayload dashboard = new DashboardUpdatePayload("A002", 200, "FINISHED", "end");
        assertEquals("A002", dashboard.getAuctionId());
        assertEquals(200, dashboard.getCurrentPrice());
        assertEquals("FINISHED", dashboard.getStatus());
        assertEquals("end", dashboard.getEndTime());
        dashboard.setAuctionId("A003");
        dashboard.setCurrentPrice(250);
        dashboard.setStatus("OPEN");
        dashboard.setEndTime("later");
        assertEquals("A003", dashboard.getAuctionId());
        assertEquals(250, dashboard.getCurrentPrice());
        assertEquals("OPEN", dashboard.getStatus());
        assertEquals("later", dashboard.getEndTime());

        PlaceBidRequest placeBid = new PlaceBidRequest();
        placeBid.setAuctionId("A004");
        placeBid.setBidderId("U001");
        placeBid.setAmount(300);
        assertEquals("A004", placeBid.getAuctionId());
        assertEquals("U001", placeBid.getBidderId());
        assertEquals(300, placeBid.getAmount());

        SubscribeAuctionRequest subscribe = new SubscribeAuctionRequest();
        subscribe.setAuctionId("A005");
        assertEquals("A005", subscribe.getAuctionId());

        WalletRequest wallet = new WalletRequest();
        wallet.setUserId("U002");
        assertEquals("U002", wallet.getUserId());

        DepositRequest deposit = new DepositRequest();
        deposit.setUserId("U003");
        deposit.setAmount(400);
        assertEquals("U003", deposit.getUserId());
        assertEquals(400, deposit.getAmount());
    }

    @Test
    void messageDtos_shouldRoundTripValues() {
        SocketMessage message = new SocketMessage(MessageType.PING, "{}");
        assertEquals(MessageType.PING, message.getType());
        assertEquals("{}", message.getPayloadJson());

        message.setType(MessageType.ERROR);
        message.setPayloadJson("{\"message\":\"bad\"}");
        assertEquals(MessageType.ERROR, message.getType());
        assertEquals("{\"message\":\"bad\"}", message.getPayloadJson());

        assertArrayEquals(new MessageType[] {
                MessageType.AUTH,
                MessageType.SUBSCRIBE_AUCTION,
                MessageType.PLACE_BID,
                MessageType.AUCTION_EVENT,
                MessageType.NOTIFICATION_EVENT,
                MessageType.DASHBOARD_UPDATE,
                MessageType.DASHBOARD_REFRESH,
                MessageType.ERROR,
                MessageType.PING
        }, MessageType.values());
    }

    @Test
    void responseDtos_shouldRoundTripValuesAndReturnSelf() {
        Response<Response<?>> baseGeneric = new Response<>();
        baseGeneric.setStatus("OK");
        assertEquals("OK", baseGeneric.getStatus());

        BaseResponse base = new BaseResponse();
        assertSame(base, base.setStatus("OK"));
        assertSame(base, base.setMessage("done"));
        assertEquals("OK", base.getStatus());
        assertEquals("done", base.getMessage());

        AddItemResponse addItem = new AddItemResponse();
        assertSame(addItem, addItem.setStatus("OK"));
        assertSame(addItem, addItem.setId("I001"));
        assertEquals("I001", addItem.getId());

        DepositResponse deposit = new DepositResponse();
        assertSame(deposit, deposit.setBalance(500));
        deposit.setMessage("paid");
        assertEquals(500, deposit.getBalance());
        assertEquals("paid", deposit.getMessage());

        ErrorPayload error = new ErrorPayload("bad");
        assertEquals("bad", error.getMessage());
        error.setMessage("worse");
        assertEquals("worse", error.getMessage());

        GetItemResponse item = new GetItemResponse();
        assertSame(item, item.setItemJson("{\"id\":\"I001\"}"));
        assertEquals("{\"id\":\"I001\"}", item.getItemJson());

        List<Auction> auctions = List.of(new Auction().setId("A001"));
        GetAllAuctionsResponse allAuctions = new GetAllAuctionsResponse();
        assertSame(allAuctions, allAuctions.setData(auctions));
        assertEquals(auctions, allAuctions.getData());

        List<Bid> bids = List.of(new Bid().setBidAmount(10));
        GetAllUserBidResponse allBids = new GetAllUserBidResponse();
        assertSame(allBids, allBids.setData(bids));
        assertEquals(bids, allBids.getData());

        User user = new User().setUsername("buyer");
        UserResponse userResponse = new UserResponse();
        assertSame(userResponse, userResponse.setData(user));
        assertEquals(user, userResponse.getData());

        List<User> users = List.of(user);
        UserListResponse userList = new UserListResponse();
        assertSame(userList, userList.setData(users));
        assertEquals(users, userList.getData());

        Map<String, Integer> stats = Map.of("users", 1);
        AdminStatsResponse adminStats = new AdminStatsResponse();
        assertSame(adminStats, adminStats.setData(stats));
        assertEquals(stats, adminStats.getData());

        List<Notification> notifications = List.of(new Notification());
        NotificationResponse notificationResponse = new NotificationResponse();
        assertSame(notificationResponse, notificationResponse.setNotifications(notifications));
        assertSame(notificationResponse, notificationResponse.setUnreadCount(2));
        assertSame(notificationResponse, notificationResponse.setMessage("new"));
        assertEquals(notifications, notificationResponse.getNotifications());
        assertEquals(2, notificationResponse.getUnreadCount());
        assertEquals("new", notificationResponse.getMessage());
    }
}
