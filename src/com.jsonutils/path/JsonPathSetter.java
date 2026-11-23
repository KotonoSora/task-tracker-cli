package path;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import exception.JsonPathException;

public final class JsonPathSetter {

    private final Object source;

    public JsonPathSetter(Object source) {
        if (source == null) {
            throw new JsonPathException(
                    "JSON source cannot be null");
        }

        this.source = source;
    }

    // ==============================
    // SET
    // ==============================

    public void set(String path, Object value) {
        setValue(path, value);
    }

    public void setValue(String path, Object value) {
        List<String> segments = JsonPathParser.parse(path);

        Object current = source;

        for (int i = 0; i < segments.size() - 1; i++) {
            String segment = segments.get(i);
            String nextSegment = segments.get(i + 1);

            if (current instanceof Map<?, ?> map) {

                Map<String, Object> target = castMap(map);

                if (!target.containsKey(segment)
                        || target.get(segment) == null) {

                    target.put(
                            segment,
                            createContainer(nextSegment));
                }

                current = target.get(segment);

            } else if (current instanceof List<?> list) {

                List<Object> target = castList(list);

                int index = validateIndex(
                        segment,
                        target.size());

                if (index == target.size()) {
                    target.add(createContainer(nextSegment));

                } else if (target.get(index) == null) {
                    target.set(
                            index,
                            createContainer(nextSegment));
                }

                current = target.get(index);

            } else {
                throw new JsonPathException(
                        "Cannot navigate path: " + path);
            }
        }

        String lastSegment = segments.getLast();

        if (current instanceof Map<?, ?> map) {

            setProperty(
                    castMap(map),
                    lastSegment,
                    value);

        } else if (current instanceof List<?> list) {

            setIndex(
                    castList(list),
                    lastSegment,
                    value);

        } else {
            throw new JsonPathException(
                    "Invalid target for path: " + path);
        }
    }

    // ==============================
    // PROPERTY
    // ==============================

    public Object setProperty(
            Map<String, Object> map,
            String key,
            Object value) {

        return map.put(key, value);
    }

    // ==============================
    // INDEX
    // ==============================

    public Object setIndex(
            List<Object> list,
            String index,
            Object value) {

        int position = validateIndex(index, list.size());

        if (position == list.size()) {
            list.add(value);
            return null;
        }

        return list.set(position, value);
    }

    // ==============================
    // CONTAINER
    // ==============================

    public Object createContainer(String nextSegment) {
        if (JsonPathParser.isIndex(nextSegment)) {
            return new ArrayList<>();
        }

        return new LinkedHashMap<String, Object>();
    }

    // ==============================
    // VALIDATE INDEX
    // ==============================

    public int validateIndex(String index, int size) {
        try {
            int position = Integer.parseInt(index);

            if (position < 0 || position > size) {
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

    // ==============================
    // TYPE CASTING
    // ==============================

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Map<?, ?> map) {
        for (Object key : map.keySet()) {
            if (!(key instanceof String)) {
                throw new JsonPathException(
                        "JSON object contains a non-string key");
            }
        }

        return (Map<String, Object>) map;
    }

    @SuppressWarnings("unchecked")
    private List<Object> castList(List<?> list) {
        return (List<Object>) list;
    }
}
