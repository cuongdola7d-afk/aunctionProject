package ddc.server.network.response;

// Payload gửi cho client khi có lỗi
public class ErrorPayload {
    private String message;

    public ErrorPayload() {}

    public ErrorPayload(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
