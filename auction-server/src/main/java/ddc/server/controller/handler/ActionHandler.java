package ddc.server.controller.handler;

import com.google.gson.Gson;

import ddc.server.config.GsonConfig;
import ddc.server.controller.RequestMessage;
import ddc.server.controller.service.AuctionService;
import ddc.server.controller.service.ItemService;
import ddc.server.controller.service.UserService;
import ddc.server.network.response.Response;

public interface ActionHandler {
    final AuctionService auctionService = new AuctionService();
    final ItemService itemService = new ItemService();
    final UserService userService = new UserService();
    final Gson gson = GsonConfig.newGson();
    Response handle (RequestMessage request);
}
