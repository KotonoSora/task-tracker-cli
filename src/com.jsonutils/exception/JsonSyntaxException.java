package exception;

public class JsonSyntaxException extends JsonParserException {

    public JsonSyntaxException(String message, int index) {
        super(message, index);
    }
}
