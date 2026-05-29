package ddc.server.network;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ddc.server.controller.handler.DeleteAccountHandler;

class DeleteAccountRouteTest {

    @Test
    @DisplayName("DELETE_ACCOUNT route should resolve to DeleteAccountHandler")
    void deleteAccountRoute_shouldResolveHandler() {
        assertInstanceOf(DeleteAccountHandler.class, RequestRouter.getHandler("DELETE_ACCOUNT"));
    }
}
