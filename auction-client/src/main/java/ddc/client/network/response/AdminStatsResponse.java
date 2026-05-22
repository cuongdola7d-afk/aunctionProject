package ddc.client.network.response;

import java.util.Map;

public class AdminStatsResponse extends Response<AdminStatsResponse> {
    private Map<String, Integer> data;

    public Map<String, Integer> getData() {
        return data;
    }

    public AdminStatsResponse setData(Map<String, Integer> data) {
        this.data = data;
        return this;
    }
}
