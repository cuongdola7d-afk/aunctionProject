package ddc.client.network.response;

public class ErrorResponse {
    private String message;

    public ErrorResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}