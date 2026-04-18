package ddc.server.controller;

public class RequestMessage {
    private String action;
    private String data;

    public RequestMessage () {}

    public RequestMessage (String action, String data) {
        this.action = action;
        this.data = data;
    }

    public String getAction () { return action; }
    public String getData () { return data; }
}
