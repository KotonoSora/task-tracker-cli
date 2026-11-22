
package parser;

import exception.JsonSyntaxException;
import exception.JsonUnexpectedEndException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonParser {

    private final String json;
    private int index = 0;

    public JsonParser(String json) {
        if (json == null) {
            throw new IllegalArgumentException("JSON cannot be null");
        }

        this.json = json;
    }

    // ==============================
    // Core methods
    // ==============================

    private boolean hasNext() {
        return index < json.length();
    }

    private char peek() {
        if (!hasNext()) {
            throw unexpectedEnd("Unexpected end of JSON");
        }

        return json.charAt(index);
    }

    private char consume() {
        if (!hasNext()) {
            throw unexpectedEnd("Unexpected end of JSON");
        }

        return json.charAt(index++);
    }

    private void expect(char expected) {
        char actual = consume();

        if (actual != expected) {
            throw syntax(
                    "Expected '" + expected + "' but found '" + actual + "'");
        }
    }

    private void skipWhitespace() {
        while (hasNext()) {
            char c = peek();

            if (c == ' ' || c == '\t' ||
                    c == '\n' || c == '\r') {
                index++;
            } else {
                break;
            }
        }
    }

    // ==============================
    // Exception helpers
    // ==============================

    private JsonSyntaxException syntax(String message) {
        return new JsonSyntaxException(message, index);
    }

    private JsonUnexpectedEndException unexpectedEnd(String message) {
        return new JsonUnexpectedEndException(message, index);
    }

    // ==============================
    // Entry point
    // ==============================

    public Object parse() {
        skipWhitespace();

        Object result = parseValue();

        skipWhitespace();

        if (hasNext()) {
            throw syntax("Unexpected character: '" + peek() + "'");
        }

        return result;
    }

    // ==============================
    // Parse value
    // ==============================

    private Object parseValue() {
        skipWhitespace();

        if (!hasNext()) {
            throw unexpectedEnd("Expected JSON value");
        }

        char c = peek();

        return switch (c) {
            case '{' -> parseObject();
            case '[' -> parseArray();
            case '"' -> parseString();
            case 't' -> parseLiteral("true", Boolean.TRUE);
            case 'f' -> parseLiteral("false", Boolean.FALSE);
            case 'n' -> parseLiteral("null", null);

            default -> {
                if (c == '-' || isDigit(c)) {
                    yield parseNumber();
                }

                throw syntax("Invalid JSON value");
            }
        };
    }

    // ==============================
    // Parse object
    // ==============================

    public Map<String, Object> parseObject() {
        Map<String, Object> result = new LinkedHashMap<>();

        expect('{');
        skipWhitespace();

        if (hasNext() && peek() == '}') {
            index++;
            return result;
        }

        while (true) {
            skipWhitespace();

            if (!hasNext()) {
                throw unexpectedEnd("Expected object key");
            }

            if (peek() != '"') {
                throw syntax("Object key must be a string");
            }

            String key = parseString();

            skipWhitespace();
            expect(':');

            Object value = parseValue();

            if (result.containsKey(key)) {
                throw syntax("Duplicate object key: " + key);
            }

            result.put(key, value);

            skipWhitespace();

            char c = consume();

            if (c == '}') {
                break;
            }

            if (c != ',') {
                throw syntax("Expected ',' or '}'");
            }
        }

        return result;
    }

    // ==============================
    // Parse array
    // ==============================

    public List<Object> parseArray() {
        List<Object> result = new ArrayList<>();

        expect('[');
        skipWhitespace();

        if (hasNext() && peek() == ']') {
            index++;
            return result;
        }

        while (true) {
            result.add(parseValue());

            skipWhitespace();

            char c = consume();

            if (c == ']') {
                break;
            }

            if (c != ',') {
                throw syntax("Expected ',' or ']'");
            }
        }

        return result;
    }

    // ==============================
    // Parse string
    // ==============================

    public String parseString() {
        expect('"');

        StringBuilder result = new StringBuilder();

        while (hasNext()) {
            char c = consume();

            if (c == '"') {
                return result.toString();
            }

            if (c < 0x20) {
                throw syntax("Unescaped control character");
            }

            if (c != '\\') {
                result.append(c);
                continue;
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

                case 'u' -> {
                    result.append((char) parseUnicode());
                }

                default -> throw syntax("Invalid escape sequence");
            }
        }

        throw unexpectedEnd("Unterminated string");
    }

    // ==============================
    // Parse Unicode
    // ==============================

    private int parseUnicode() {
        int value = 0;

        for (int i = 0; i < 4; i++) {
            if (!hasNext()) {
                throw unexpectedEnd("Incomplete Unicode escape");
            }

            char c = consume();

            int digit = Character.digit(c, 16);

            if (digit == -1) {
                throw syntax("Invalid Unicode escape");
            }

            value = value * 16 + digit;
        }

        return value;
    }

    // ==============================
    // Parse number
    // ==============================

    public Number parseNumber() {
        int start = index;

        if (hasNext() && peek() == '-') {
            index++;
        }

        if (!hasNext()) {
            throw unexpectedEnd("Incomplete number");
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

        String value = json.substring(start, index);

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw syntax("Invalid number");
        }
    }

    // ==============================
    // Parse literal
    // ==============================

    private Object parseLiteral(String expected, Object value) {
        for (int i = 0; i < expected.length(); i++) {

            if (!hasNext()) {
                throw unexpectedEnd("Incomplete literal: " + expected);
            }

            if (consume() != expected.charAt(i)) {
                throw syntax("Invalid literal: " + expected);
            }
        }

        return value;
    }

    // ==============================
    // Utility methods
    // ==============================

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isDigitOneToNine(char c) {
        return c >= '1' && c <= '9';
    }
}
