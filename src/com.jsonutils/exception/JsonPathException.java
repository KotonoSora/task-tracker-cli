package exception;

public class JsonPathException extends RuntimeException {

    public JsonPathException(String message) {
        super(message);
    }

    public JsonPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonPathException(Throwable cause) {
        super(cause);
    }
}
