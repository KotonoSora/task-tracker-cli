package path;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import exception.JsonPathException;

public final class JsonPathGetter {

    private final Object source;

    public JsonPathGetter(Object source) {
        this.source = Objects.requireNonNull(
                source,
                "JSON source cannot be null");
    }

    // ==============================
    // CORE GETTERS
    // ==============================

    public Object get(String path) {
        return resolve(path);
    }

    public Object getValue(String path) {
        return resolve(path);
    }

    public String getString(String path) {
        Object value = resolve(path);

        if (value == null) {
            return null;
        }

        if (value instanceof String string) {
            return string;
        }

        throw typeMismatch(path, "String", value);
    }

    public Integer getInteger(String path) {
        Number number = getNumber(path);

        if (number == null) {
            return null;
        }

        try {
            return toBigDecimal(number).intValueExact();
        } catch (ArithmeticException exception) {
            throw new JsonPathException(
                    "Value cannot be converted to Integer at path: " + path,
                    exception);
        }
    }

    public Long getLong(String path) {
        Number number = getNumber(path);

        if (number == null) {
            return null;
        }

        try {
            return toBigDecimal(number).longValueExact();
        } catch (ArithmeticException exception) {
            throw new JsonPathException(
                    "Value cannot be converted to Long at path: " + path,
                    exception);
        }
    }

    public Boolean getBoolean(String path) {
        Object value = resolve(path);

        if (value == null) {
            return null;
        }

        if (value instanceof Boolean bool) {
            return bool;
        }

        throw typeMismatch(path, "Boolean", value);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getMap(String path) {
        Object value = resolve(path);

        if (value == null) {
            return null;
        }

        if (value instanceof Map<?, ?> map) {
            for (Object key : map.keySet()) {
                if (!(key instanceof String)) {
                    throw new JsonPathException(
                            "JSON object contains a non-string key");
                }
            }

            return (Map<String, Object>) map;
        }

        throw typeMismatch(path, "Map", value);
    }

    @SuppressWarnings("unchecked")
    public List<Object> getList(String path) {
        Object value = resolve(path);

        if (value == null) {
            return null;
        }

        if (value instanceof List<?> list) {
            return (List<Object>) list;
        }

        throw typeMismatch(path, "List", value);
    }

    public Integer getLength(String path) {
        Object value = resolve(path);

        if (value == null) {
            return null;
        }

        return switch (value) {
            case String string -> string.length();
            case Map<?, ?> map -> map.size();
            case List<?> list -> list.size();
            default -> throw typeMismatch(
                    path,
                    "String, Map or List",
                    value);
        };
    }

    // ==============================
    // NUMBER GETTERS
    // ==============================

    public Double getDouble(String path) {
        Number number = getNumber(path);

        return number == null ? null : number.doubleValue();
    }

    public Float getFloat(String path) {
        Number number = getNumber(path);

        return number == null ? null : number.floatValue();
    }

    public BigDecimal getBigDecimal(String path) {
        Number number = getNumber(path);

        return number == null ? null : toBigDecimal(number);
    }

    public BigInteger getBigInteger(String path) {
        Number number = getNumber(path);

        if (number == null) {
            return null;
        }

        try {
            return toBigDecimal(number).toBigIntegerExact();
        } catch (ArithmeticException exception) {
            throw new JsonPathException(
                    "Value cannot be converted to BigInteger at path: " + path,
                    exception);
        }
    }

    // ==============================
    // DEFAULT VALUE
    // ==============================

    public Object getOrDefault(
            String path,
            Object defaultValue) {

        Object value = resolve(path);

        return value != null ? value : defaultValue;
    }

    public String getStringOrDefault(
            String path,
            String defaultValue) {

        String value = getString(path);

        return value != null ? value : defaultValue;
    }

    public Integer getIntegerOrDefault(
            String path,
            Integer defaultValue) {

        Integer value = getInteger(path);

        return value != null ? value : defaultValue;
    }

    // ==============================
    // CHECKERS
    // ==============================

    public boolean exists(String path) {
        try {
            resolve(path);
            return true;
        } catch (JsonPathException exception) {
            return false;
        }
    }

    public boolean isNull(String path) {
        return resolve(path) == null;
    }

    // ==============================
    // INTERNAL METHODS
    // ==============================

    private Number getNumber(String path) {
        Object value = resolve(path);

        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number;
        }

        throw typeMismatch(path, "Number", value);
    }

    private Object resolve(String path) {
        List<String> segments = JsonPathParser.parse(path);

        Object current = source;

        for (String segment : segments) {
            if (current instanceof Map<?, ?> map) {

                if (!map.containsKey(segment)) {
                    throw new JsonPathException(
                            "Property not found: " + path);
                }

                current = map.get(segment);

            } else if (current instanceof List<?> list) {

                int index = parseIndex(segment, list.size());

                current = list.get(index);

            } else {
                throw new JsonPathException(
                        "Cannot resolve segment '%s' in path '%s'"
                                .formatted(segment, path));
            }
        }

        return current;
    }

    private int parseIndex(String segment, int size) {
        try {
            int index = Integer.parseInt(segment);

            if (index < 0 || index >= size) {
                throw new JsonPathException(
                        "Array index out of bounds: " + index);
            }

            return index;

        } catch (NumberFormatException exception) {
            throw new JsonPathException(
                    "Invalid array index: " + segment,
                    exception);
        }
    }

    private BigDecimal toBigDecimal(Number number) {
        if (number instanceof BigDecimal decimal) {
            return decimal;
        }

        if (number instanceof BigInteger integer) {
            return new BigDecimal(integer);
        }

        return new BigDecimal(number.toString());
    }

    private JsonPathException typeMismatch(
            String path,
            String expected,
            Object actual) {

        return new JsonPathException(
                "Invalid JSON type at path '%s'. Expected %s but got %s"
                        .formatted(
                                path,
                                expected,
                                actual.getClass().getSimpleName()));
    }
}
