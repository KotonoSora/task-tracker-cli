package path;

import java.util.List;
import java.util.Map;

import exception.JsonPathException;

public final class JsonPathRemove {

    private final Object source;

    public JsonPathRemove(Object source) {
        if (source == null) {
            throw new JsonPathException(
                    "JSON source cannot be null");
        }

        this.source = source;
    }

    // ==============================
    // REMOVE
    // ==============================

    public Object remove(String path) {
        List<String> segments = JsonPathParser.parse(path);

        Object parent = getParent(segments);

        String lastSegment = segments.getLast();

        if (parent instanceof Map<?, ?> map) {
            return removeProperty(map, lastSegment);
        }

        if (parent instanceof List<?> list) {
            return removeIndex(list, lastSegment);
        }

        throw new JsonPathException(
                "Invalid target for path: " + path);
    }

    // ==============================
    // GET PARENT
    // ==============================

    public Object getParent(String path) {
        List<String> segments = JsonPathParser.parse(path);
        return getParent(segments);
    }

    private Object getParent(List<String> segments) {
        Object current = source;

        for (int i = 0; i < segments.size() - 1; i++) {
            String segment = segments.get(i);

            if (current instanceof Map<?, ?> map) {

                if (!map.containsKey(segment)) {
                    throw new JsonPathException(
                            "Property not found: " + segment);
                }

                current = map.get(segment);

            } else if (current instanceof List<?> list) {

                int index = validateIndex(
                        segment,
                        list.size());

                current = list.get(index);

            } else {
                throw new JsonPathException(
                        "Cannot navigate through: " + segment);
            }

            if (current == null) {
                throw new JsonPathException(
                        "Cannot navigate through null: " + segment);
            }
        }

        return current;
    }

    // ==============================
    // REMOVE PROPERTY
    // ==============================

    public Object removeProperty(
            Map<?, ?> map,
            String key) {

        if (!map.containsKey(key)) {
            throw new JsonPathException(
                    "Property not found: " + key);
        }

        return map.remove(key);
    }

    // ==============================
    // REMOVE INDEX
    // ==============================

    public Object removeIndex(
            List<?> list,
            String index) {

        int position = validateIndex(
                index,
                list.size() - 1);

        return list.remove(position);
    }

    // ==============================
    // REMOVE IF EXISTS
    // ==============================

    public boolean removeIfExists(String path) {
        try {
            remove(path);
            return true;
        } catch (JsonPathException exception) {
            return false;
        }
    }

    // ==============================
    // VALIDATE INDEX
    // ==============================

    private int validateIndex(String index, int maxIndex) {
        try {
            int position = Integer.parseInt(index);

            if (position < 0 || position > maxIndex) {
                throw new JsonPathException(
                        "Array index out of bounds: " + position);
            }

            return position;

        } catch (NumberFormatException exception) {
            throw new JsonPathException(
                    "Invalid array index: " + index,
                    exception);
        }
    }
}
