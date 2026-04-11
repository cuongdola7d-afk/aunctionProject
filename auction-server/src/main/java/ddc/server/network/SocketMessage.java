package ddc.server.network;

public class SocketMessage {
    private MessageType type;
    private String payloadJson;

    public SocketMessage() {
    }

    public SocketMessage(MessageType type, String payloadJson) {
        this.type = type;
        this.payloadJson = payloadJson;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public void setPayloadJson(String payloadJson) {
        this.payloadJson = payloadJson;
    }
}