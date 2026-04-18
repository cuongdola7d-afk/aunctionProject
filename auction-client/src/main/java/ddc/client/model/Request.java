package ddc.client.model;

import com.google.gson.Gson;

public class Request {
    private String action;
    private String data;

    public Request (String action, Object dataObj) {
        this.action = action;
        this.data = new Gson().toJson(dataObj);
    }

    public String getAction () { return action; }
    public String getData () { return data; }
}
