package lexer;

import exception.JsonSyntaxException;
import exception.JsonUnexpectedEndException;
import token.JsonToken;

public class JsonLexer {

    private final String source;
    private int index = 0;

    public JsonLexer(String source) {
        if (source == null) {
            throw new IllegalArgumentException(
                    "JSON source cannot be null");
        }

        this.source = source;
    }

    // =====================================
    // Main method
    // =====================================

    public JsonToken nextToken() {

        skipWhitespace();

        if (!hasNext()) {
            return createToken(
                    JsonToken.Type.EOF,
                    null,
                    index);
        }

        int start = index;
        char c = consume();

        return switch (c) {

            // Structural characters

            case '{' -> createToken(
                    JsonToken.Type.LEFT_BRACE, "{", start);

            case '}' -> createToken(
                    JsonToken.Type.RIGHT_BRACE, "}", start);

            case '[' -> createToken(
                    JsonToken.Type.LEFT_BRACKET, "[", start);

            case ']' -> createToken(
                    JsonToken.Type.RIGHT_BRACKET, "]", start);

            case ':' -> createToken(
                    JsonToken.Type.COLON, ":", start);

            case ',' -> createToken(
                    JsonToken.Type.COMMA, ",", start);

            // String

            case '"' -> scanString(start);

            // Literals

            case 't' -> scanLiteral(
                    "rue",
                    JsonToken.Type.TRUE,
                    "true",
                    start);

            case 'f' -> scanLiteral(
                    "alse",
                    JsonToken.Type.FALSE,
                    "false",
                    start);

            case 'n' -> scanLiteral(
                    "ull",
                    JsonToken.Type.NULL,
                    "null",
                    start);

            // Number

            default -> {

                if (c == '-' || isDigit(c)) {
                    yield scanNumber(start);
                }

                throw syntax(
                        "Unexpected character: '" + c + "'");
            }
        };
    }

    // =====================================
    // String
    // =====================================

    private JsonToken scanString(int start) {

        StringBuilder result = new StringBuilder();

        while (hasNext()) {

            char c = consume();

            // End of string

            if (c == '"') {

                return createToken(
                        JsonToken.Type.STRING,
                        result.toString(),
                        start);
            }

            // Control characters are forbidden

            if (c < 0x20) {
                throw syntax(
                        "Unescaped control character");
            }

            if (c != '\\') {
                result.append(c);
                continue;
            }

            // Escape sequence

            if (!hasNext()) {
                throw unexpectedEnd(
                        "Incomplete escape sequence");
            }

            char escaped = consume();

            switch (escaped) {

                case '"' -> result.append('"');

                case '\\' -> result.append('\\');

                case '/' -> result.append('/');

                case 'b' -> result.append('\b');

                case 'f' -> result.append('\f');

                case 'n' -> result.append('\n');

                case 'r' -> result.append('\r');

                case 't' -> result.append('\t');

                case 'u' -> result.append(
                        (char) scanUnicode());

                default -> throw syntax(
                        "Invalid escape sequence: \\" + escaped);
            }
        }

        throw unexpectedEnd("Unterminated string");
    }

    // =====================================
    // Unicode
    // =====================================

    private int scanUnicode() {

        int value = 0;

        for (int i = 0; i < 4; i++) {

            if (!hasNext()) {
                throw unexpectedEnd(
                        "Incomplete Unicode escape");
            }

            char c = consume();

            int digit = hexValue(c);

            if (digit == -1) {
                throw syntax(
                        "Invalid Unicode escape");
            }

            value = value * 16 + digit;
        }

        return value;
    }

    private int hexValue(char c) {

        if (c >= '0' && c <= '9') {
            return c - '0';
        }

        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }

        if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        }

        return -1;
    }

    // =====================================
    // Number
    // =====================================

    private JsonToken scanNumber(int start) {

        index = start;

        // Negative sign
        if (peek() == '-') {
            index++;
            if (!hasNext()) {
                throw unexpectedEnd("Incomplete number");
            }
        }

        // Integer part
        if (peek() == '0') {
            index++;
            if (hasNext() && isDigit(peek())) {
                throw syntax("Leading zeros are not allowed");
            }
        } else if (isDigitOneToNine(peek())) {
            while (hasNext() && isDigit(peek())) {
                index++;
            }
        } else {
            throw syntax("Invalid integer part");
        }

        // Fraction part
        if (hasNext() && peek() == '.') {
            index++;
            if (!hasNext() || !isDigit(peek())) {
                throw syntax("Invalid fraction");
            }
            while (hasNext() && isDigit(peek())) {
                index++;
            }
        }

        // Exponent part
        if (hasNext() && (peek() == 'e' || peek() == 'E')) {
            index++;
            if (hasNext() && (peek() == '+' || peek() == '-')) {
                index++;
            }
            if (!hasNext() || !isDigit(peek())) {
                throw syntax("Invalid exponent");
            }
            while (hasNext() && isDigit(peek())) {
                index++;
            }
        }

        return createToken(
                JsonToken.Type.NUMBER,
                source.substring(start, index),
                start);
    }

    // =====================================
    // Literal
    // =====================================

    private JsonToken scanLiteral(
            String remaining,
            JsonToken.Type type,
            String value,
            int start) {

        for (int i = 0; i < remaining.length(); i++) {

            if (!hasNext()) {
                throw unexpectedEnd(
                        "Incomplete literal: " + value);
            }

            if (consume() != remaining.charAt(i)) {
                throw syntax(
                        "Invalid literal: " + value);
            }
        }

        return createToken(type, value, start);
    }

    // =====================================
    // Whitespace
    // =====================================

    private void skipWhitespace() {

        while (hasNext()) {

            char c = peek();

            if (c == 0x20 ||
                    c == 0x09 ||
                    c == 0x0A ||
                    c == 0x0D) {

                index++;

            } else {
                break;
            }
        }
    }

    // =====================================
    // Character operations
    // =====================================

    private boolean hasNext() {
        return index < source.length();
    }

    private char peek() {

        if (!hasNext()) {
            throw unexpectedEnd(
                    "Unexpected end of JSON");
        }

        return source.charAt(index);
    }

    private char consume() {

        if (!hasNext()) {
            throw unexpectedEnd(
                    "Unexpected end of JSON");
        }

        return source.charAt(index++);
    }

    // =====================================
    // Character validation
    // =====================================

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isDigitOneToNine(char c) {
        return c >= '1' && c <= '9';
    }

    // =====================================
    // Token factory
    // =====================================

    private JsonToken createToken(
            JsonToken.Type type,
            String value,
            int position) {

        return new JsonToken(
                type,
                value,
                position);
    }

    // =====================================
    // Exception handling
    // =====================================

    private JsonSyntaxException syntax(String message) {

        return new JsonSyntaxException(
                message,
                index);
    }

    private JsonUnexpectedEndException unexpectedEnd(
            String message) {

        return new JsonUnexpectedEndException(
                message,
                index);
    }
}
