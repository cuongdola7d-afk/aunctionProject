package ddc.server.controller.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import ddc.server.controller.RequestMessage;
import ddc.server.model.user.User;
import ddc.server.network.response.Response;

class LoginHandlerTest {
    private final LoginHandler handler = new LoginHandler();
    private final Gson gson = new Gson();

    @Test
    void handle_shouldReturnInvalidInputWhenUsernameMissing() {
        User user = new User().setPassword("password123");

        Response<?> response = handler.handle(request(user));

        assertEquals("INVALID_INPUT", response.getStatus());
    }

    @Test
    void handle_shouldReturnInvalidInputWhenPasswordMissing() {
        User user = new User().setUsername("alice");

        Response<?> response = handler.handle(request(user));

        assertEquals("INVALID_INPUT", response.getStatus());
    }

    @Test
    void handle_shouldReturnPasswordLessThan8WhenPasswordTooShort() {
        User user = new User()
                .setUsername("alice")
                .setPassword("1234567");

        Response<?> response = handler.handle(request(user));

        assertEquals("PASSWORD_LESS_THAN_8", response.getStatus());
    }

    private RequestMessage request(User user) {
        return new RequestMessage("LOGIN", gson.toJson(user));
    }
}
