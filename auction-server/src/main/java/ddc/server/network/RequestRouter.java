package ddc.server.network;

import java.util.HashMap;
import java.util.Map;

import ddc.server.controller.handler.*;

public class RequestRouter {
    private static final Map<String, ActionHandler> routes = new HashMap<>();

    static {
        routes.put("LOGIN", new LoginHandler());
        routes.put("REGISTER", new RegisterHandler());
        routes.put("ADD_ITEM", new AddItemHandler());
        routes.put("GET_ITEM", new GetItemHandler());
        routes.put("CREATE_AUCTION", new CreateAuctionHandler());
        routes.put("GET_ALL", new GetAllAuctionHandler());
        routes.put("UPDATE_PASSWORD", new ChangePasswordHandler());
        routes.put("UPDATE_PROFILE", new UpdateProfileHandler());
        routes.put("ADMIN_STATS", new AdminGetStatsHandler());
        routes.put("ADMIN_GET_USERS", new AdminGetUsersHandler());
        routes.put("ADMIN_UPDATE_USER_STATUS", new AdminUpdateUserStatusHandler());
        routes.put("ADMIN_DELETE_USER", new AdminDeleteUserHandler());
        routes.put("ADMIN_CANCEL_AUCTION", new AdminCancelAuctionHandler());
        routes.put("GET_NOTIFICATIONS", new GetNotificationsHandler());
        routes.put("MARK_NOTIFICATION_READ", new MarkNotificationReadHandler());
    }

    public static ActionHandler getHandler(String action) {
        return routes.get(action);
    }
}
