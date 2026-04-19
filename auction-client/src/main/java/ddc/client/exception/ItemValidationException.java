package ddc.client.exception;

public class ItemValidationException extends Exception {
    public ItemValidationException(String message) {
        super(message);
    }

    // Ngoại lệ về giá (Selling)
    public static class InvalidPriceException extends ItemValidationException {
        public InvalidPriceException(String message) {
            super(message);
        }
    }

    // Ngoại lệ về thời gian đấu giá (Selling)
    public static class InvalidDurationException extends ItemValidationException {
        public InvalidDurationException(String message) {
            super(message);
        }
    }

    // Ngoại lệ về thông tin bị thiếu (Selling)
    public static class MissingFieldException extends ItemValidationException {
        public MissingFieldException(String message) {
            super(message);
        }
    }
}