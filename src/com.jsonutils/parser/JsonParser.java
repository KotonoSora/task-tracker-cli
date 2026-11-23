package parser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import exception.JsonSyntaxException;
import exception.JsonUnexpectedEndException;
import lexer.JsonLexer;
import token.JsonToken;

public class JsonParser {

    private static final int DEFAULT_MAX_DEPTH = 256;

    private final JsonLexer lexer;
    private final DuplicateKeyPolicy duplicateKeyPolicy;
    private final int maxDepth;

    private JsonToken current;
    private int depth = 0;

    // =====================================
    // Constructors
    // =====================================

    public JsonParser(String json) {
        this(
                json,
                DuplicateKeyPolicy.REJECT,
                DEFAULT_MAX_DEPTH);
    }

    public JsonParser(
            String json,
            DuplicateKeyPolicy duplicateKeyPolicy) {
        this(
                json,
                duplicateKeyPolicy,
                DEFAULT_MAX_DEPTH);
    }

    public JsonParser(
            String json,
            DuplicateKeyPolicy duplicateKeyPolicy,
            int maxDepth) {

        if (duplicateKeyPolicy == null) {
            throw new IllegalArgumentException(
                    "Duplicate key policy cannot be null");
        }

        if (maxDepth < 1) {
            throw new IllegalArgumentException(
                    "Maximum depth must be greater than zero");
        }

        this.lexer = new JsonLexer(json);
        this.duplicateKeyPolicy = duplicateKeyPolicy;
        this.maxDepth = maxDepth;

        this.current = lexer.nextToken();
    }

    // =====================================
    // Entry point
    // =====================================

    public Object parse() {

        Object result = parseValue();

        if (!current.is(JsonToken.Type.EOF)) {
            throw syntax(
                    "Unexpected token: " + current.type());
        }

        return result;
    }

    // =====================================
    // Parse value
    // =====================================

    private Object parseValue() {

        return switch (current.type()) {

            case LEFT_BRACE -> parseObject();

            case LEFT_BRACKET -> parseArray();

            case STRING -> parseString();

            case NUMBER -> parseNumber();

            case TRUE -> parseBoolean(true);

            case FALSE -> parseBoolean(false);

            case NULL -> parseNull();

            case EOF -> throw unexpectedEnd(
                    "Expected JSON value");

            default -> throw syntax(
                    "Unexpected token: " + current.type());
        };
    }

    // =====================================
    // Parse object
    // =====================================

    private Map<String, Object> parseObject() {

        enterContainer();

        try {

            Map<String, Object> result = new LinkedHashMap<>();

            expect(JsonToken.Type.LEFT_BRACE);

            // Empty object

            if (match(JsonToken.Type.RIGHT_BRACE)) {
                return result;
            }

            while (true) {

                // Object key

                if (!current.is(JsonToken.Type.STRING)) {
                    throw syntax(
                            "Object key must be a string");
                }

                String key = current.value();

                advance();

                // Colon

                expect(JsonToken.Type.COLON);

                // Value

                Object value = parseValue();

                // Duplicate key handling

                putObjectValue(result, key, value);

                // End object

                if (match(JsonToken.Type.RIGHT_BRACE)) {
                    break;
                }

                // Separator

                expect(JsonToken.Type.COMMA);

                // Trailing comma

                if (current.is(JsonToken.Type.RIGHT_BRACE)) {
                    throw syntax(
                            "Trailing comma is not allowed");
                }
            }

            return result;

        } finally {
            exitContainer();
        }
    }

    // =====================================
    // Parse array
    // =====================================

    private List<Object> parseArray() {

        enterContainer();

        try {

            List<Object> result = new ArrayList<>();

            expect(JsonToken.Type.LEFT_BRACKET);

            // Empty array

            if (match(JsonToken.Type.RIGHT_BRACKET)) {
                return result;
            }

            while (true) {

                // Array element

                result.add(parseValue());

                // End array

                if (match(JsonToken.Type.RIGHT_BRACKET)) {
                    break;
                }

                // Separator

                expect(JsonToken.Type.COMMA);

                // Trailing comma

                if (current.is(JsonToken.Type.RIGHT_BRACKET)) {
                    throw syntax(
                            "Trailing comma is not allowed");
                }
            }

            return result;

        } finally {
            exitContainer();
        }
    }

    // =====================================
    // Duplicate key handling
    // =====================================

    private void putObjectValue(
            Map<String, Object> object,
            String key,
            Object value) {

        boolean duplicate = object.containsKey(key);

        if (!duplicate) {
            object.put(key, value);
            return;
        }

        switch (duplicateKeyPolicy) {

            case REJECT -> throw syntax(
                    "Duplicate object key: " + key);

            case KEEP_FIRST -> {
                // Keep existing value
            }

            case KEEP_LAST -> object.put(key, value);
        }
    }

    // =====================================
    // Parse string
    // =====================================

    private String parseString() {

        String value = current.value();

        advance();

        return value;
    }

    // =====================================
    // Parse number
    // =====================================

    private Number parseNumber() {

        String value = current.value();

        advance();

        try {

            return new BigDecimal(value);

        } catch (NumberFormatException e) {

            throw syntax(
                    "Invalid number: " + value);
        }
    }

    // =====================================
    // Parse boolean
    // =====================================

    private Boolean parseBoolean(boolean value) {

        advance();

        return value;
    }

    // =====================================
    // Parse null
    // =====================================

    private Object parseNull() {

        advance();

        return null;
    }

    // =====================================
    // Depth management
    // =====================================

    private void enterContainer() {

        if (depth >= maxDepth) {
            throw syntax(
                    "Maximum JSON nesting depth exceeded: " +
                            maxDepth);
        }

        depth++;
    }

    private void exitContainer() {
        depth--;
    }

    // =====================================
    // Token operations
    // =====================================

    private void advance() {

        current = lexer.nextToken();
    }

    private boolean match(JsonToken.Type expected) {

        if (!current.is(expected)) {
            return false;
        }

        advance();

        return true;
    }

    private void expect(JsonToken.Type expected) {

        if (current.is(JsonToken.Type.EOF)) {
            throw unexpectedEnd(
                    "Expected " + expected);
        }

        if (!current.is(expected)) {
            throw syntax(
                    "Expected " + expected +
                            " but found " + current.type());
        }

        advance();
    }

    // =====================================
    // Exception handling
    // =====================================

    private JsonSyntaxException syntax(String message) {

        return new JsonSyntaxException(
                message,
                current.position());
    }

    private JsonUnexpectedEndException unexpectedEnd(
            String message) {

        return new JsonUnexpectedEndException(
                message,
                current.position());
    }
}
