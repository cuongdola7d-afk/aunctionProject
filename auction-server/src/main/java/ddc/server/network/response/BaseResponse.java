package ddc.server.network.response;

public class BaseResponse extends Response<BaseResponse> {
    
    private String message;

    public String getMessage() {
        return message;
    }

    public BaseResponse setMessage(String message) {
        this.message = message;
        return this;
    }
}