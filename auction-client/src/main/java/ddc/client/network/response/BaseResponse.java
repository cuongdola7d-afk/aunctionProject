package ddc.client.network.response;

public class BaseResponse {
    private final String status;

    public String getStatus () { return status; }
    
    public BaseResponse (String status) {
        this.status = status;
    }
}
