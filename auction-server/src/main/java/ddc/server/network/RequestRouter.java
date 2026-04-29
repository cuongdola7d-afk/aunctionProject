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
        routes.put("UPDATE_PASSWORD", new ChangePasswordHandler());
    }

    public static ActionHandler getHandler (String action) {
        return routes.get(action);
    }
}
