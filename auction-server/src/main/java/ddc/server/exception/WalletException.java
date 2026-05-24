package ddc.server.exception;

public class WalletException extends Exception {
    public WalletException(String message) {
        super(message);
    }

    public WalletException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class InvalidAmountException extends WalletException {
        public InvalidAmountException(String message) {
            super(message);
        }
    }

    public static class InsufficientBalanceException extends WalletException {
        public InsufficientBalanceException(String message) {
            super(message);
        }
    }

    public static class OperationFailedException extends WalletException {
        public OperationFailedException(String message) {
            super(message);
        }
    }
}
