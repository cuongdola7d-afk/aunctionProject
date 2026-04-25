package ddc.client.model;

import com.google.gson.Gson;

import ddc.client.config.GsonConfig;

public class Request {
    private transient final Gson gson = GsonConfig.newGson();
    private String action;
    private String data;

    public Request (String action, Object dataObj) {
        this.action = action;
        this.data = gson.toJson(dataObj);
    }

    public String getAction () { return action; }
    public String getData () { return data; }
}
