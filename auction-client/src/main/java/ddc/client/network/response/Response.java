package ddc.client.network.response;

public class Response<T> {
    public String status;

    public String getStatus () { return status; }

    protected T self () {
        return (T) this;
    }

    public T setStatus (String status) {
        this.status = status;
        return self();
    }
}
