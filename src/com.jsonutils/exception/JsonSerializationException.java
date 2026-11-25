package exception;

public class JsonSerializationException extends RuntimeException {

    // =====================================
    // Constructors
    // =====================================

    public JsonSerializationException() {
        super();
    }

    public JsonSerializationException(String message) {
        super(message);
    }

    public JsonSerializationException(
            String message,
            Throwable cause) {
        super(message, cause);
    }

    public JsonSerializationException(Throwable cause) {
        super(cause);
    }
}
