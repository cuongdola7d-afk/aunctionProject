package ddc.server.network.response;

public class DepositResponse extends BaseResponse {
    private double balance;

    public double getBalance() {
        return balance;
    }

    public DepositResponse setBalance(double balance) {
        this.balance = balance;
        return this;
    }
}
