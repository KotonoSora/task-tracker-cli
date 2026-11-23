package path;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exception.JsonPathException;

public final class JsonPathParser {

    private static final Pattern PATH_PATTERN = Pattern.compile("[^.[\\]]+|\\[(\\d+)\\]");

    private JsonPathParser() {
    }

    public static List<String> parse(String path) {
        if (path == null || path.isBlank()) {
            throw new JsonPathException(
                    "JSON path cannot be null or blank");
        }

        List<String> segments = new ArrayList<>();

        Matcher matcher = PATH_PATTERN.matcher(path);

        int position = 0;

        while (matcher.find()) {
            String separator = path.substring(position, matcher.start());

            if (!separator.isEmpty()
                    && !separator.equals(".")) {
                throw new JsonPathException(
                        "Invalid JSON path: " + path);
            }

            String segment = matcher.group(1) != null
                    ? matcher.group(1)
                    : matcher.group();

            segments.add(segment);
            position = matcher.end();
        }

        if (segments.isEmpty()
                || position != path.length()
                || path.startsWith(".")
                || path.endsWith(".")
                || path.contains("..")) {
            throw new JsonPathException(
                    "Invalid JSON path: " + path);
        }

        return List.copyOf(segments);
    }

    public static boolean isIndex(String segment) {
        return segment != null
                && segment.matches("\\d+");
    }
}
