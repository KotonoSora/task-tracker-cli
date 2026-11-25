package exception;

public class JsonFormatException extends RuntimeException {

    private final int position;

    public JsonFormatException(String message) {
        this(message, -1);
    }

    public JsonFormatException(
            String message,
            int position) {
        super(buildMessage(message, position));
        this.position = position;
    }

    public JsonFormatException(
            String message,
            int position,
            Throwable cause) {
        super(buildMessage(message, position), cause);
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    private static String buildMessage(
            String message,
            int position) {

        if (message == null || message.isBlank()) {
            message = "JSON formatting error";
        }

        if (position < 0) {
            return message;
        }

        return message + " at position " + position;
    }
}
