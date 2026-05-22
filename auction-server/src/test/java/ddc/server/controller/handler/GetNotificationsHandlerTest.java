// package ddc.server.controller.handler;

// import static org.junit.jupiter.api.Assertions.*;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.DisplayName;


// import ddc.server.controller.RequestMessage;
// import ddc.server.network.response.Response;

// @DisplayName("GetNotificationsHandler - Unit Tests")
// class GetNotificationsHandlerTest {
//     private final GetNotificationsHandler handler = new GetNotificationsHandler();

//     @Test
//     @DisplayName("getNotifications - JSON lỗi → trả FAIL")
//     void testHandle_MalformedJson() {
//         RequestMessage req = new RequestMessage("GET_NOTIFICATIONS", "BAD_JSON");
//         Response resp = handler.handle(req);
//         assertEquals("FAIL", resp.getStatus());
//     }

//     @Test
//     @DisplayName("getNotifications - Thiếu userId → NullPointerException → FAIL")
//     void testHandle_MissingUserId() {
//         String json = "{\"limit\":10,\"offset\":0}";
//         RequestMessage req = new RequestMessage("GET_NOTIFICATIONS", json);
//         Response resp = handler.handle(req);
//         // data.get("userId") ném NullPointerException → catch → FAIL
//         assertEquals("FAIL", resp.getStatus());
//     }

//     @Test
//     @DisplayName("getNotifications - Có đầy đủ fields → không crash, trả response")
//     void testHandle_ValidData() {
//         String json = "{\"userId\":\"U999_NONEXISTENT\",\"limit\":10,\"offset\":0}";
//         RequestMessage req = new RequestMessage("GET_NOTIFICATIONS", json);
//         Response resp = handler.handle(req);
//         assertNotNull(resp.getStatus());
//     }

//     @Test
//     @DisplayName("getNotifications - Không có limit/offset → dùng default không crash")
//     void testHandle_DefaultPagination() {
//         String json = "{\"userId\":\"U999_NONEXISTENT\"}";
//         RequestMessage req = new RequestMessage("GET_NOTIFICATIONS", json);
//         Response resp = handler.handle(req);
//         assertNotNull(resp.getStatus());
//     }
// }
