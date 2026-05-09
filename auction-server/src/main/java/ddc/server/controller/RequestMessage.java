package ddc.server.controller;

public class RequestMessage {
    private String action;
    private String data;
    private byte[] imageData;

    public RequestMessage () {}

    public RequestMessage (String action, String data) {
        this.action = action;
        this.data = data;
    }

    public String getAction () { return action; }
    public String getData () { return data; }

    public byte[] getImageData() { return imageData; }
    public void setImageData(byte[] imageData) { this.imageData = imageData; }
}