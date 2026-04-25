package ddc.server.exception;

public class ItemValidationException extends Exception {
    public ItemValidationException(String message) {
        super(message);
    }

    public static class InvalidValueException extends ItemValidationException {
        public InvalidValueException(String message) {
            super(message);
        }
    }

    // Ngoại lệ về thông tin bị thiếu (Selling)
    public static class MissingFieldException extends ItemValidationException {
        public MissingFieldException(String message) {
            super(message);
        }
    }

    public static class InvalidCategoryException extends ItemValidationException{
        public InvalidCategoryException(String message){
            super(message);
        }
    }
}