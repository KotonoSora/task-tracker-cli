
package exception;

public class JsonUnexpectedEndException extends JsonParserException {

    public JsonUnexpectedEndException(String message, int index) {
        super(message, index);
    }
}
