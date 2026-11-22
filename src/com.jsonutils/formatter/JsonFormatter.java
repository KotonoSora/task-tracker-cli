package formatter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

public class JsonFormatter {

    private static final int DEFAULT_INDENT_SIZE = 4;

    private static final char OBJECT_OPEN = '{';
    private static final char OBJECT_CLOSE = '}';

    private static final char ARRAY_OPEN = '[';
    private static final char ARRAY_CLOSE = ']';

    private static final char STRING_QUOTE = '"';
    private static final char ESCAPE = '\\';

    private static final char COLON = ':';
    private static final char COMMA = ',';

    private final int indentSize;

    // =====================================
    // Constructors
    // =====================================

    public JsonFormatter() {
        this(DEFAULT_INDENT_SIZE);
    }

    public JsonFormatter(int indentSize) {

        if (indentSize < 0) {
            throw new IllegalArgumentException(
                    "Indent size cannot be negative");
        }

        this.indentSize = indentSize;
    }

    // =====================================
    // Format
    // =====================================

    public String format(String json) {
        return prettyPrint(json);
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

            // Inside JSON string

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

            // Start JSON string

            if (c == STRING_QUOTE) {

                inString = true;

                result.append(c);

                continue;
            }

            // Ignore whitespace outside strings

            if (isWhitespace(c)) {
                continue;
            }

            switch (c) {

                // Object and array opening

                case OBJECT_OPEN, ARRAY_OPEN -> {

                    result.append(c);

                    stack.push(c);

                    if (isEmptyContainer(json, i)) {

                        char closing = c == OBJECT_OPEN
                                ? OBJECT_CLOSE
                                : ARRAY_CLOSE;

                        result.append(closing);

                        stack.pop();

                        i = findNextNonWhitespace(json, i + 1);

                    } else {

                        depth++;

                        result.append('\n');

                        result.append(indent(depth));
                    }
                }

                // Object and array closing

                case OBJECT_CLOSE, ARRAY_CLOSE -> {

                    validateClosing(stack, c);

                    depth--;

                    if (!endsWithOpening(result)) {

                        result.append('\n');

                        result.append(indent(depth));
                    }

                    result.append(c);

                    stack.pop();
                }

                // Comma

                case COMMA -> {

                    result.append(c);

                    result.append('\n');

                    result.append(indent(depth));
                }

                // Colon

                case COLON -> {

                    result.append(c);

                    result.append(' ');
                }

                // Other characters

                default -> result.append(c);
            }
        }

        // Validate final state

        if (inString) {
            throw new IllegalArgumentException(
                    "Unterminated JSON string");
        }

        if (!stack.isEmpty()) {
            throw new IllegalArgumentException(
                    "Unclosed JSON container: " + stack.peek());
        }

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

            // Preserve everything inside strings

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

            // Start string

            if (c == STRING_QUOTE) {

                inString = true;

                result.append(c);

                continue;
            }

            // Remove whitespace

            if (isWhitespace(c)) {
                continue;
            }

            // Track containers

            switch (c) {

                case OBJECT_OPEN, ARRAY_OPEN ->
                    stack.push(c);

                case OBJECT_CLOSE, ARRAY_CLOSE ->
                    validateClosing(stack, c);

                default -> {
                    // No operation
                }
            }

            result.append(c);

            if (c == OBJECT_CLOSE || c == ARRAY_CLOSE) {
                stack.pop();
            }
        }

        validateFinalState(inString, stack);

        return result.toString();
    }

    // =====================================
    // Indent
    // =====================================

    public String indent(int depth) {

        if (depth < 0) {
            throw new IllegalArgumentException(
                    "Indent depth cannot be negative");
        }

        int spaces;

        try {
            spaces = Math.multiplyExact(
                    depth,
                    indentSize);
        } catch (ArithmeticException e) {
            throw new IllegalArgumentException(
                    "Indent depth is too large",
                    e);
        }

        return " ".repeat(spaces);
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

                            result.append(c);
                            result.append(value.charAt(++i));

                        } else {

                            appendUnicodeEscape(result, c);
                        }

                    } else if (Character.isLowSurrogate(c)) {

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

        char expected = current == OBJECT_OPEN
                ? OBJECT_CLOSE
                : ARRAY_CLOSE;

        return json.charAt(next) == expected;
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
            char closing) {

        if (stack.isEmpty()) {

            throw new IllegalArgumentException(
                    "Unexpected closing character: " + closing);
        }

        char opening = stack.peek();

        boolean valid = switch (closing) {

            case OBJECT_CLOSE -> opening == OBJECT_OPEN;

            case ARRAY_CLOSE -> opening == ARRAY_OPEN;

            default -> false;
        };

        if (!valid) {

            throw new IllegalArgumentException(
                    "Mismatched JSON container: " + closing);
        }
    }

    // =====================================
    // Result Validation
    // =====================================

    private void validateFinalState(
            boolean inString,
            Deque<Character> stack) {

        if (inString) {

            throw new IllegalArgumentException(
                    "Unterminated JSON string");
        }

        if (!stack.isEmpty()) {

            throw new IllegalArgumentException(
                    "Unclosed JSON container: " + stack.peek());
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

            throw new IllegalArgumentException(
                    "JSON input cannot be null");
        }

        if (json.isBlank()) {

            throw new IllegalArgumentException(
                    "JSON input cannot be empty");
        }
    }
}
