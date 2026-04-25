package ddc.server.controller.handler;

import com.google.gson.Gson;

import ddc.server.config.GsonConfig;
import ddc.server.controller.RequestMessage;
import ddc.server.network.response.Response;

public interface ActionHandler {
    final Gson gson = GsonConfig.newGson();
    Response handle (RequestMessage request);
}
