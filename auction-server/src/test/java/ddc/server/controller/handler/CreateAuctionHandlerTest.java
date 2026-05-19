package ddc.server.controller.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import ddc.server.controller.RequestMessage;
import ddc.server.network.response.Response;

class CreateAuctionHandlerTest {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CreateAuctionHandler handler = new CreateAuctionHandler();

    @Test
    void handle_shouldReturnInvalidInputWhenDataBlank() {
        Response<?> response = handler.handle(new RequestMessage("CREATE_AUCTION", " "));

        assertEquals("INVALID_INPUT", response.getStatus());
    }

    @Test
    void handle_shouldReturnInvalidInputWhenItemMissing() {
        String data = """
                {
                  "currentPrice": 100,
                  "startTime": "%s",
                  "endTime": "%s"
                }
                """.formatted(futureMinutes(5), futureMinutes(30));

        Response<?> response = handler.handle(request(data));

        assertEquals("INVALID_INPUT", response.getStatus());
    }

    @Test
    void handle_shouldReturnInvalidInputWhenItemIdMissing() {
        String data = """
                {
                  "item": { "category": "GENERAL" },
                  "currentPrice": 100,
                  "startTime": "%s",
                  "endTime": "%s"
                }
                """.formatted(futureMinutes(5), futureMinutes(30));

        Response<?> response = handler.handle(request(data));

        assertEquals("INVALID_INPUT", response.getStatus());
    }

    @Test
    void handle_shouldReturnInvalidInputWhenStartTimeMissing() {
        String data = """
                {
                  "item": { "id": "I001", "category": "GENERAL" },
                  "currentPrice": 100,
                  "endTime": "%s"
                }
                """.formatted(futureMinutes(30));

        Response<?> response = handler.handle(request(data));

        assertEquals("INVALID_INPUT", response.getStatus());
    }

    @Test
    void handle_shouldReturnInvalidInputWhenEndTimeIsBeforeStartTime() {
        String data = """
                {
                  "item": { "id": "I001", "category": "GENERAL" },
                  "currentPrice": 100,
                  "startTime": "%s",
                  "endTime": "%s"
                }
                """.formatted(futureMinutes(120), futureMinutes(60));

        Response<?> response = handler.handle(request(data));

        assertEquals("INVALID_INPUT", response.getStatus());
    }

    @Test
    void handle_shouldReturnInvalidInputWhenCurrentPriceNotPositive() {
        String data = """
                {
                  "item": { "id": "I001", "category": "GENERAL" },
                  "currentPrice": 0,
                  "startTime": "%s",
                  "endTime": "%s"
                }
                """.formatted(futureMinutes(5), futureMinutes(30));

        Response<?> response = handler.handle(request(data));

        assertEquals("INVALID_INPUT", response.getStatus());
    }

    private RequestMessage request(String data) {
        return new RequestMessage("CREATE_AUCTION", data);
    }

    private String futureMinutes(int minutes) {
        return LocalDateTime.now().plusMinutes(minutes).format(FORMATTER);
    }
}
