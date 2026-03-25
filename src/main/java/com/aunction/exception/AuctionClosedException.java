package com.aunction.exception;

public class AuctionClosedException extends Exception {
    public AuctionClosedException(String message) {
        super(message);
    }
}