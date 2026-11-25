package path;

import java.util.List;
import java.util.Map;

import exception.JsonPathException;

public final class JsonPathHas {

    private final Object source;

    public JsonPathHas(Object source) {
        if (source == null) {
            throw new JsonPathException(
                    "JSON source cannot be null");
        }

        this.source = source;
    }

    // ==============================
    // HAS
    // ==============================

    public boolean has(String path) {
        return hasPath(path);
    }

    // ==============================
    // HAS PATH
    // ==============================

    public boolean hasPath(String path) {
        List<String> segments = JsonPathParser.parse(path);

        Object current = source;

        for (String segment : segments) {

            if (current instanceof Map<?, ?> map) {

                if (!map.containsKey(segment)) {
                    return false;
                }

                current = map.get(segment);

            } else if (current instanceof List<?> list) {

                int index = parseIndex(segment);

                if (!containsIndex(list, index)) {
                    return false;
                }

                current = list.get(index);

            } else {
                return false;
            }
        }

        return true;
    }

    // ==============================
    // CONTAINS PROPERTY
    // ==============================

    public boolean containsProperty(String path) {
        List<String> segments = JsonPathParser.parse(path);

        Object parent = getParent(segments);

        String property = segments.getLast();

        if (!(parent instanceof Map<?, ?> map)) {
            return false;
        }

        return map.containsKey(property);
    }

    // ==============================
    // CONTAINS INDEX
    // ==============================

    public boolean containsIndex(String path) {
        List<String> segments = JsonPathParser.parse(path);

        Object parent = getParent(segments);

        String index = segments.getLast();

        if (!(parent instanceof List<?> list)) {
            return false;
        }

        int position = parseIndex(index);

        return position >= 0 && position < list.size();
    }

    // ==============================
    // HAS VALUE
    // ==============================

    public boolean hasValue(String path) {
        if (!hasPath(path)) {
            return false;
        }

        return getValue(path) != null;
    }

    // ==============================
    // GET OR DEFAULT
    // ==============================

    public Object getOrDefault(
            String path,
            Object defaultValue) {

        if (!hasPath(path)) {
            return defaultValue;
        }

        return getValue(path);
    }

    // ==============================
    // INTERNAL METHODS
    // ==============================

    private Object getParent(List<String> segments) {
        Object current = source;

        for (int i = 0; i < segments.size() - 1; i++) {
            String segment = segments.get(i);

            if (current instanceof Map<?, ?> map) {

                if (!map.containsKey(segment)) {
                    return null;
                }

                current = map.get(segment);

            } else if (current instanceof List<?> list) {

                int index = parseIndex(segment);

                if (index >= list.size()) {
                    return null;
                }

                current = list.get(index);

            } else {
                return null;
            }

            if (current == null) {
                return null;
            }
        }

        return current;
    }

    private Object getValue(String path) {
        Object current = source;

        for (String segment : JsonPathParser.parse(path)) {

            if (current instanceof Map<?, ?> map) {
                current = map.get(segment);

            } else if (current instanceof List<?> list) {
                current = list.get(parseIndex(segment));

            } else {
                return null;
            }
        }

        return current;
    }

    private int parseIndex(String index) {
        try {
            int position = Integer.parseInt(index);

            if (position < 0) {
                throw new JsonPathException(
                        "Invalid array index: " + index);
            }

            return position;

        } catch (NumberFormatException exception) {
            throw new JsonPathException(
                    "Invalid array index: " + index,
                    exception);
        }
    }

    private boolean containsIndex(List<?> list, int index) {
        return index >= 0 && index < list.size();
    }
}
