package ddc.server.exception;

public class AuctionClosedException extends Exception {
    public AuctionClosedException(String message) {
        super(message);
    }
}