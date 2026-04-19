package ddc.server.network;

import java.util.HashMap;
import java.util.Map;

import ddc.server.controller.handler.ActionHandler;
import ddc.server.controller.handler.AddItemHandler;
import ddc.server.controller.handler.LoginHandler;
import ddc.server.controller.handler.RegisterHandler;

public class RequestRouter {
    private static final Map<String, ActionHandler> routes = new HashMap<>();

    static {
        routes.put("LOGIN", new LoginHandler());
        routes.put("REGISTER", new RegisterHandler());
        routes.put("ADD_ITEM", new AddItemHandler());
    }

    public static ActionHandler getHandler (String action) {
        return routes.get(action);
    }
}
