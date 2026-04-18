package ddc.server.controller.handler;

import ddc.server.controller.RequestMessage;

public interface ActionHandler {
    String handle (RequestMessage request);
}
