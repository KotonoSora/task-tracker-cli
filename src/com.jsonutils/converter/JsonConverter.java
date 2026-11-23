package converter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import array.JsonArray;
import object.JsonObject;

public class JsonConverter {

    // =====================================
    // Convert To String
    // =====================================

    public String toString(Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof String string) {
            return string;
        }

        if (value instanceof Character character) {
            return character.toString();
        }

        if (value instanceof Number
                || value instanceof Boolean) {
            return value.toString();
        }

        throw new IllegalArgumentException(
                "Cannot convert to String: "
                        + value.getClass().getName());
    }

    // =====================================
    // Convert To Integer
    // =====================================

    public Integer toInteger(Object value) {

        if (value == null) {
            return null;
        }

        try {

            if (value instanceof Integer integer) {
                return integer;
            }

            if (value instanceof Number number) {
                return new BigDecimal(
                        number.toString()).intValueExact();
            }

            if (value instanceof String string) {
                return new BigDecimal(
                        string.trim()).intValueExact();
            }

            if (value instanceof Boolean bool) {
                return bool ? 1 : 0;
            }

        } catch (NumberFormatException
                | ArithmeticException e) {

            throw conversionException(
                    value,
                    "Integer",
                    e);
        }

        throw conversionException(value, "Integer");
    }

    // =====================================
    // Convert To Long
    // =====================================

    public Long toLong(Object value) {

        if (value == null) {
            return null;
        }

        try {

            if (value instanceof Long longValue) {
                return longValue;
            }

            if (value instanceof Number number) {
                return new BigDecimal(
                        number.toString()).longValueExact();
            }

            if (value instanceof String string) {
                return new BigDecimal(
                        string.trim()).longValueExact();
            }

            if (value instanceof Boolean bool) {
                return bool ? 1L : 0L;
            }

        } catch (NumberFormatException
                | ArithmeticException e) {

            throw conversionException(
                    value,
                    "Long",
                    e);
        }

        throw conversionException(value, "Long");
    }

    // =====================================
    // Convert To Double
    // =====================================

    public Double toDouble(Object value) {

        if (value == null) {
            return null;
        }

        try {

            if (value instanceof Double doubleValue) {
                return doubleValue;
            }

            if (value instanceof Number number) {
                return number.doubleValue();
            }

            if (value instanceof String string) {
                double result = Double.parseDouble(
                        string.trim());

                if (!Double.isFinite(result)) {
                    throw new ArithmeticException(
                            "Non-finite number");
                }

                return result;
            }

            if (value instanceof Boolean bool) {
                return bool ? 1.0 : 0.0;
            }

        } catch (NumberFormatException
                | ArithmeticException e) {

            throw conversionException(
                    value,
                    "Double",
                    e);
        }

        throw conversionException(value, "Double");
    }

    // =====================================
    // Convert To Map
    // =====================================

    public Map<String, Object> toMap(Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof JsonObject object) {
            return convertObject(object);
        }

        if (value instanceof Map<?, ?> map) {

            Map<String, Object> result = new LinkedHashMap<>();

            for (Map.Entry<?, ?> entry : map.entrySet()) {

                if (!(entry.getKey() instanceof String key)) {
                    throw new IllegalArgumentException(
                            "Map key must be a String");
                }

                result.put(
                        key,
                        toConvert(entry.getValue()));
            }

            return result;
        }

        throw conversionException(value, "Map");
    }

    // =====================================
    // Convert To List
    // =====================================

    public List<Object> toList(Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof JsonArray array) {

            return convertArray(array);
        }

        if (value instanceof List<?> list) {

            List<Object> result = new ArrayList<>();

            for (Object element : list) {
                result.add(toConvert(element));
            }

            return result;
        }

        throw conversionException(value, "List");
    }

    // =====================================
    // General Convert
    // =====================================

    public Object toConvert(Object value) {

        if (value == null) {
            return null;
        }

        if (value instanceof JsonObject object) {
            return convertObject(object);
        }

        if (value instanceof JsonArray array) {
            return convertArray(array);
        }

        if (value instanceof Map<?, ?> map) {
            return toMap(map);
        }

        if (value instanceof List<?> list) {
            return toList(list);
        }

        if (value instanceof String
                || value instanceof Number
                || value instanceof Boolean) {
            return value;
        }

        if (value instanceof Character character) {
            return character.toString();
        }

        throw conversionException(value, "Java type");
    }

    // =====================================
    // Convert JsonObject
    // =====================================

    private Map<String, Object> convertObject(
            JsonObject object) {

        Map<String, Object> result = new LinkedHashMap<>();

        for (Map.Entry<String, Object> entry : object.asMap().entrySet()) {

            result.put(
                    entry.getKey(),
                    toConvert(entry.getValue()));
        }

        return result;
    }

    // =====================================
    // Convert JsonArray
    // =====================================

    private List<Object> convertArray(JsonArray array) {

        List<Object> result = new ArrayList<>();

        for (Object value : array.values()) {
            result.add(toConvert(value));
        }

        return result;
    }

    // =====================================
    // Conversion Exception
    // =====================================

    private IllegalArgumentException conversionException(
            Object value,
            String targetType) {

        return new IllegalArgumentException(
                "Cannot convert "
                        + value.getClass().getName()
                        + " to "
                        + targetType);
    }

    // =====================================
    // Conversion Exception With Cause
    // =====================================

    private IllegalArgumentException conversionException(
            Object value,
            String targetType,
            Throwable cause) {

        return new IllegalArgumentException(
                "Cannot convert "
                        + value
                        + " to "
                        + targetType,
                cause);
    }
}
