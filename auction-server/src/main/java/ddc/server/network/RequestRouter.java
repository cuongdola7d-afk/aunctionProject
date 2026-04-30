package ddc.server.network;

import java.util.HashMap;
import java.util.Map;

import ddc.server.controller.handler.ActionHandler;
import ddc.server.controller.handler.AddItemHandler;
import ddc.server.controller.handler.ChangePasswordHandler;
import ddc.server.controller.handler.CreateAuctionHandler;
import ddc.server.controller.handler.GetAllAuctionHandler;
import ddc.server.controller.handler.GetItemHandler;
import ddc.server.controller.handler.LoginHandler;
import ddc.server.controller.handler.RegisterHandler;

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
    }

    public static ActionHandler getHandler (String action) {
        return routes.get(action);
    }
 }
