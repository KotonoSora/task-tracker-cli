package exception;

public class JsonParserException extends RuntimeException {

    private final int index;

    public JsonParserException(String message, int index) {
        super(message + " at position " + index);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
