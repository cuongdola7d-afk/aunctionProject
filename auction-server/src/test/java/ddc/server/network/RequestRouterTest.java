package ddc.server.network;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

import ddc.server.controller.handler.AddItemHandler;
import ddc.server.controller.handler.ChangePasswordHandler;
import ddc.server.controller.handler.CreateAuctionHandler;
import ddc.server.controller.handler.DepositHandler;
import ddc.server.controller.handler.GetWalletBalanceHandler;
import ddc.server.controller.handler.GetItemHandler;
import ddc.server.controller.handler.LoginHandler;
import ddc.server.controller.handler.RegisterHandler;
import ddc.server.controller.handler.UpdateProfileHandler;
import ddc.server.controller.handler.admin.GetAllAuctionHandler;

class RequestRouterTest {

    @Test
    void getHandler_shouldReturnConfiguredHandlerForKnownActions() {
        assertInstanceOf(LoginHandler.class, RequestRouter.getHandler("LOGIN"));
        assertInstanceOf(RegisterHandler.class, RequestRouter.getHandler("REGISTER"));
        assertInstanceOf(AddItemHandler.class, RequestRouter.getHandler("ADD_ITEM"));
        assertInstanceOf(GetItemHandler.class, RequestRouter.getHandler("GET_ITEM"));
        assertInstanceOf(CreateAuctionHandler.class, RequestRouter.getHandler("CREATE_AUCTION"));
        assertInstanceOf(GetAllAuctionHandler.class, RequestRouter.getHandler("GET_ALL_AUCTIONS"));
        assertInstanceOf(ChangePasswordHandler.class, RequestRouter.getHandler("UPDATE_PASSWORD"));
        assertInstanceOf(UpdateProfileHandler.class, RequestRouter.getHandler("UPDATE_PROFILE"));
        assertInstanceOf(GetWalletBalanceHandler.class, RequestRouter.getHandler("GET_WALLET_BALANCE"));
        assertInstanceOf(DepositHandler.class, RequestRouter.getHandler("DEPOSIT"));
    }

    @Test
    void getHandler_shouldReturnNullForUnknownOrNullAction() {
        assertNull(RequestRouter.getHandler("UNKNOWN"));
        assertNull(RequestRouter.getHandler(null));
    }
}
