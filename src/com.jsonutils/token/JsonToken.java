package token;

public record JsonToken(
        Type type,
        String value,
        int position) {

    public enum Type {

        // Structural tokens

        LEFT_BRACE, // {
        RIGHT_BRACE, // }

        LEFT_BRACKET, // [
        RIGHT_BRACKET, // ]

        COLON, // :
        COMMA, // ,

        // Value tokens

        STRING,
        NUMBER,

        TRUE,
        FALSE,
        NULL,

        // End of input

        EOF
    }

    // ==============================
    // Constructor validation
    // ==============================

    public JsonToken {

        if (type == null) {
            throw new IllegalArgumentException(
                    "Token type cannot be null");
        }

        if (position < 0) {
            throw new IllegalArgumentException(
                    "Token position cannot be negative");
        }

        if (type == Type.EOF) {
            if (value != null) {
                throw new IllegalArgumentException(
                        "EOF token must have null value");
            }
        } else if (value == null) {
            throw new IllegalArgumentException(
                    "Token value cannot be null");
        }
    }

    // ==============================
    // Token helpers
    // ==============================

    public boolean is(Type expected) {
        return type == expected;
    }

    public boolean isValue() {
        return switch (type) {
            case STRING, NUMBER, TRUE, FALSE, NULL -> true;
            default -> false;
        };
    }

    public boolean isStructural() {
        return switch (type) {
            case LEFT_BRACE,
                    RIGHT_BRACE,
                    LEFT_BRACKET,
                    RIGHT_BRACKET,
                    COLON,
                    COMMA ->
                true;

            default -> false;
        };
    }

    public boolean isEnd() {
        return type == Type.EOF;
    }

    @Override
    public String toString() {
        return "JsonToken{" +
                "type=" + type +
                ", value='" + value + '\'' +
                ", position=" + position +
                '}';
    }
}
