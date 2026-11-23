package formatter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

import exception.JsonFormatException;

public class JsonFormatter {

    private static final char OBJECT_OPEN = '{';
    private static final char OBJECT_CLOSE = '}';

    private static final char ARRAY_OPEN = '[';
    private static final char ARRAY_CLOSE = ']';

    private static final char STRING_QUOTE = '"';
    private static final char ESCAPE = '\\';

    private static final char COLON = ':';
    private static final char COMMA = ',';

    private final JsonFormatOptions options;

    // =====================================
    // Constructors
    // =====================================

    public JsonFormatter() {
        this(JsonFormatOptions.defaults());
    }

    public JsonFormatter(int indentSize) {
        this(
                JsonFormatOptions.builder()
                        .indentSize(indentSize)
                        .build());
    }

    public JsonFormatter(JsonFormatOptions options) {
        this.options = Objects.requireNonNull(
                options,
                "Format options cannot be null");
    }

    // =====================================
    // Get Options
    // =====================================

    public JsonFormatOptions getOptions() {
        return options;
    }

    // =====================================
    // Format
    // =====================================

    public String format(String json) {
        return format(json, JsonFormatStyle.PRETTY);
    }

    public String format(
            String json,
            JsonFormatStyle style) {

        Objects.requireNonNull(
                style,
                "Format style cannot be null");

        return switch (style) {

            case PRETTY -> prettyPrint(json);

            case COMPACT -> minify(json);
        };
    }

    // =====================================
    // Pretty Print
    // =====================================

    public String prettyPrint(String json) {

        validateInput(json);

        StringBuilder result = new StringBuilder();

        Deque<Character> stack = new ArrayDeque<>();

        boolean inString = false;
        boolean escaped = false;

        int depth = 0;

        for (int i = 0; i < json.length(); i++) {

            char c = json.charAt(i);

            if (inString) {

                result.append(c);

                if (escaped) {
                    escaped = false;
                    continue;
                }

                if (c == ESCAPE) {
                    escaped = true;
                    continue;
                }

                if (c == STRING_QUOTE) {
                    inString = false;
                }

                continue;
            }

            if (c == STRING_QUOTE) {

                inString = true;

                result.append(c);

                continue;
            }

            if (isWhitespace(c)) {
                continue;
            }

            switch (c) {

                case OBJECT_OPEN, ARRAY_OPEN -> {

                    result.append(c);

                    stack.push(c);

                    if (isEmptyContainer(json, i)) {

                        result.append(
                                getClosingCharacter(c));

                        stack.pop();

                        i = findNextNonWhitespace(
                                json,
                                i + 1);

                    } else {

                        if (c == ARRAY_OPEN
                                && options.arrayStyle() == JsonFormatOptions.ArrayStyle.SINGLE_LINE) {

                            continue;
                        }

                        depth++;

                        appendNewLine(result);

                        result.append(indent(depth));
                    }
                }

                case OBJECT_CLOSE, ARRAY_CLOSE -> {

                    validateClosing(stack, c, i);

                    char opening = stack.peek();

                    boolean singleLineArray = opening == ARRAY_OPEN
                            && options.arrayStyle() == JsonFormatOptions.ArrayStyle.SINGLE_LINE;

                    stack.pop();

                    if (singleLineArray) {

                        result.append(c);

                        continue;
                    }

                    depth--;

                    if (!endsWithOpening(result)) {

                        appendNewLine(result);

                        result.append(indent(depth));
                    }

                    result.append(c);
                }

                case COMMA -> {

                    result.append(COMMA);

                    boolean singleLineArray = !stack.isEmpty()
                            && stack.peek() == ARRAY_OPEN
                            && options.arrayStyle() == JsonFormatOptions.ArrayStyle.SINGLE_LINE;

                    if (singleLineArray) {

                        if (options.spaceAfterComma()) {
                            result.append(' ');
                        }

                    } else {

                        appendNewLine(result);

                        result.append(indent(depth));
                    }
                }

                case COLON -> {

                    result.append(COLON);

                    if (options.spaceAfterColon()) {
                        result.append(' ');
                    }
                }

                default -> result.append(c);
            }
        }

        validateFinalState(inString, stack, json.length());

        return result.toString();
    }

    // =====================================
    // Minify
    // =====================================

    public String minify(String json) {

        validateInput(json);

        StringBuilder result = new StringBuilder();

        Deque<Character> stack = new ArrayDeque<>();

        boolean inString = false;
        boolean escaped = false;

        for (int i = 0; i < json.length(); i++) {

            char c = json.charAt(i);

            if (inString) {

                result.append(c);

                if (escaped) {
                    escaped = false;
                    continue;
                }

                if (c == ESCAPE) {
                    escaped = true;
                    continue;
                }

                if (c == STRING_QUOTE) {
                    inString = false;
                }

                continue;
            }

            if (c == STRING_QUOTE) {

                inString = true;

                result.append(c);

                continue;
            }

            if (isWhitespace(c)) {
                continue;
            }

            switch (c) {

                case OBJECT_OPEN, ARRAY_OPEN ->
                    stack.push(c);

                case OBJECT_CLOSE, ARRAY_CLOSE -> {
                    validateClosing(stack, c, i);
                    stack.pop();
                }

                default -> {
                }
            }

            result.append(c);
        }

        validateFinalState(inString, stack, json.length());

        return result.toString();
    }

    // =====================================
    // Indent
    // =====================================

    public String indent(int depth) {

        if (depth < 0) {
            throw new JsonFormatException(
                    "Indent depth cannot be negative");
        }

        try {

            int size = Math.multiplyExact(
                    depth,
                    options.indentSize());

            return options.indentCharacter().repeat(size);

        } catch (ArithmeticException e) {

            throw new JsonFormatException(
                    "Indent depth is too large",
                    -1,
                    e);
        }
    }

    // =====================================
    // Escape String
    // =====================================

    public String escapeString(String value) {

        Objects.requireNonNull(
                value,
                "String value cannot be null");

        StringBuilder result = new StringBuilder();

        result.append(STRING_QUOTE);

        UnicodePolicy policy = options.unicodePolicy();

        for (int i = 0; i < value.length(); i++) {

            char c = value.charAt(i);

            switch (c) {

                case STRING_QUOTE ->
                    result.append("\\\"");

                case ESCAPE ->
                    result.append("\\\\");

                case '\b' ->
                    result.append("\\b");

                case '\f' ->
                    result.append("\\f");

                case '\n' ->
                    result.append("\\n");

                case '\r' ->
                    result.append("\\r");

                case '\t' ->
                    result.append("\\t");

                default -> {

                    if (c < 0x20) {

                        appendUnicodeEscape(result, c);

                    } else if (Character.isHighSurrogate(c)) {

                        if (i + 1 < value.length()
                                && Character.isLowSurrogate(
                                        value.charAt(i + 1))) {

                            char low = value.charAt(i + 1);

                            if (policy == UnicodePolicy.ESCAPE_ALL
                                    || policy == UnicodePolicy.ESCAPE_NON_ASCII) {

                                appendUnicodeEscape(result, c);
                                appendUnicodeEscape(result, low);

                            } else {

                                result.append(c);
                                result.append(low);
                            }

                            i++;

                        } else {

                            appendUnicodeEscape(result, c);
                        }

                    } else if (Character.isLowSurrogate(c)) {

                        appendUnicodeEscape(result, c);

                    } else if (policy == UnicodePolicy.ESCAPE_ALL
                            || (policy == UnicodePolicy.ESCAPE_NON_ASCII
                                    && c > 0x7F)) {

                        appendUnicodeEscape(result, c);

                    } else {

                        result.append(c);
                    }
                }
            }
        }

        result.append(STRING_QUOTE);

        return result.toString();
    }

    // =====================================
    // Unicode Escape
    // =====================================

    private void appendUnicodeEscape(
            StringBuilder result,
            char c) {

        result.append("\\u");

        String hex = Integer.toHexString(c);

        result.append(
                "0".repeat(4 - hex.length()));

        result.append(hex);
    }

    // =====================================
    // Empty Container
    // =====================================

    private boolean isEmptyContainer(
            String json,
            int position) {

        int next = findNextNonWhitespace(
                json,
                position + 1);

        if (next >= json.length()) {
            return false;
        }

        char current = json.charAt(position);

        return json.charAt(next) == getClosingCharacter(current);
    }

    // =====================================
    // Closing Character
    // =====================================

    private char getClosingCharacter(char opening) {

        return switch (opening) {

            case OBJECT_OPEN -> OBJECT_CLOSE;

            case ARRAY_OPEN -> ARRAY_CLOSE;

            default -> throw new JsonFormatException(
                    "Invalid opening character: " + opening);
        };
    }

    // =====================================
    // Find Next Non-Whitespace
    // =====================================

    private int findNextNonWhitespace(
            String json,
            int position) {

        int index = position;

        while (index < json.length()
                && isWhitespace(json.charAt(index))) {

            index++;
        }

        return index;
    }

    // =====================================
    // Closing Validation
    // =====================================

    private void validateClosing(
            Deque<Character> stack,
            char closing,
            int position) {

        if (stack.isEmpty()) {

            throw new JsonFormatException(
                    "Unexpected closing character: " + closing,
                    position);
        }

        char opening = stack.peek();

        boolean valid = switch (closing) {

            case OBJECT_CLOSE -> opening == OBJECT_OPEN;

            case ARRAY_CLOSE -> opening == ARRAY_OPEN;

            default -> false;
        };

        if (!valid) {

            throw new JsonFormatException(
                    "Mismatched JSON container: " + closing,
                    position);
        }
    }

    // =====================================
    // Final State Validation
    // =====================================

    private void validateFinalState(
            boolean inString,
            Deque<Character> stack,
            int position) {

        if (inString) {

            throw new JsonFormatException(
                    "Unterminated JSON string",
                    position);
        }

        if (!stack.isEmpty()) {

            throw new JsonFormatException(
                    "Unclosed JSON container: " + stack.peek(),
                    position);
        }
    }

    // =====================================
    // Result Helpers
    // =====================================

    private boolean endsWithOpening(StringBuilder result) {

        if (result.isEmpty()) {
            return false;
        }

        char last = result.charAt(result.length() - 1);

        return last == OBJECT_OPEN || last == ARRAY_OPEN;
    }

    // =====================================
    // Line Separator
    // =====================================

    private void appendNewLine(StringBuilder result) {
        result.append(options.lineSeparator());
    }

    // =====================================
    // Whitespace
    // =====================================

    private boolean isWhitespace(char c) {

        return c == 0x20
                || c == 0x09
                || c == 0x0A
                || c == 0x0D;
    }

    // =====================================
    // Input Validation
    // =====================================

    private void validateInput(String json) {

        if (json == null) {

            throw new JsonFormatException(
                    "JSON input cannot be null");
        }

        if (json.isBlank()) {

            throw new JsonFormatException(
                    "JSON input cannot be empty");
        }
    }
}
