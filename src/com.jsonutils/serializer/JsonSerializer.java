package serializer;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import array.JsonArray;
import exception.JsonSerializationException;
import formatter.JsonFormatStyle;
import formatter.JsonFormatter;
import object.JsonObject;

public class JsonSerializer {

    private final JsonFormatter formatter;

    // =====================================
    // Constructors
    // =====================================

    public JsonSerializer() {
        this(new JsonFormatter());
    }

    public JsonSerializer(JsonFormatter formatter) {
        this.formatter = Objects.requireNonNull(
                formatter,
                "JsonFormatter cannot be null");
    }

    // =====================================
    // Serializer
    // =====================================

    public String serializer(Object value) {
        return serializer(value, JsonFormatStyle.PRETTY);
    }

    public String serializer(
            Object value,
            JsonFormatStyle style) {

        Objects.requireNonNull(
                style,
                "Format style cannot be null");

        StringBuilder output = new StringBuilder();

        Set<Object> visited = java.util.Collections.newSetFromMap(
                new IdentityHashMap<>());

        serializeValue(value, output, visited);

        return formatter.format(output.toString(), style);
    }

    // =====================================
    // Serialize Value
    // =====================================

    private void serializeValue(
            Object value,
            StringBuilder output,
            Set<Object> visited) {

        if (value == null) {

            serializerNull(output);

        } else if (value instanceof JsonObject object) {

            checkCircularReference(object, visited);

            try {
                serializerObject(object, output, visited);
            } finally {
                visited.remove(object);
            }

        } else if (value instanceof JsonArray jsonArray) {

            checkCircularReference(jsonArray, visited);

            try {
                serializerArray(
                        jsonArray.values(),
                        output,
                        visited);
            } finally {
                visited.remove(jsonArray);
            }

        } else if (value instanceof Collection<?> collection) {

            checkCircularReference(collection, visited);

            try {
                serializerArray(collection, output, visited);
            } finally {
                visited.remove(collection);
            }

        } else if (value.getClass().isArray()) {

            checkCircularReference(value, visited);

            try {
                serializerJavaArray(value, output, visited);
            } finally {
                visited.remove(value);
            }

        } else if (value instanceof String string) {

            serializerString(string, output);

        } else if (value instanceof Character character) {

            serializerString(character.toString(), output);

        } else if (value instanceof Number number) {

            serializerNumber(number, output);

        } else if (value instanceof Boolean bool) {

            serializerBoolean(bool, output);

        } else {

            throw new JsonSerializationException(
                    "Unsupported JSON type: "
                            + value.getClass().getName());
        }
    }

    // =====================================
    // Serialize Object
    // =====================================

    public void serializerObject(JsonObject object) {

        StringBuilder output = new StringBuilder();

        serializerObject(
                object,
                output,
                java.util.Collections.newSetFromMap(
                        new IdentityHashMap<>()));
    }

    private void serializerObject(
            JsonObject object,
            StringBuilder output,
            Set<Object> visited) {

        Objects.requireNonNull(
                object,
                "JsonObject cannot be null");

        output.append('{');

        boolean first = true;

        for (Map.Entry<String, Object> entry : object.asMap().entrySet()) {

            if (!first) {
                output.append(',');
            }

            output.append(
                    formatter.escapeString(entry.getKey()));

            output.append(':');

            serializeValue(
                    entry.getValue(),
                    output,
                    visited);

            first = false;
        }

        output.append('}');
    }

    // =====================================
    // Serialize Array
    // =====================================

    public void serializerArray() {
        throw new UnsupportedOperationException(
                "Use serializer(Object) to serialize an array");
    }

    private void serializerArray(
            Collection<?> array,
            StringBuilder output,
            Set<Object> visited) {

        output.append('[');

        boolean first = true;

        for (Object value : array) {

            if (!first) {
                output.append(',');
            }

            serializeValue(
                    value,
                    output,
                    visited);

            first = false;
        }

        output.append(']');
    }

    // =====================================
    // Serialize Java Array
    // =====================================

    private void serializerJavaArray(
            Object array,
            StringBuilder output,
            Set<Object> visited) {

        output.append('[');

        int length = Array.getLength(array);

        for (int i = 0; i < length; i++) {

            if (i > 0) {
                output.append(',');
            }

            serializeValue(
                    Array.get(array, i),
                    output,
                    visited);
        }

        output.append(']');
    }

    // =====================================
    // Serialize String
    // =====================================

    public void serializerString() {
        throw new UnsupportedOperationException(
                "Use serializer(Object) to serialize a string");
    }

    private void serializerString(
            String value,
            StringBuilder output) {

        output.append(
                formatter.escapeString(value));
    }

    // =====================================
    // Serialize Number
    // =====================================

    public void serializerNumber() {
        throw new UnsupportedOperationException(
                "Use serializer(Object) to serialize a number");
    }

    private void serializerNumber(
            Number value,
            StringBuilder output) {

        if (value instanceof Double d
                && !Double.isFinite(d)) {

            throw new JsonSerializationException(
                    "JSON number cannot be NaN or Infinity");
        }

        if (value instanceof Float f
                && !Float.isFinite(f)) {

            throw new JsonSerializationException(
                    "JSON number cannot be NaN or Infinity");
        }

        if (value instanceof BigDecimal
                || value instanceof BigInteger
                || value instanceof Byte
                || value instanceof Short
                || value instanceof Integer
                || value instanceof Long
                || value instanceof Float
                || value instanceof Double) {

            output.append(value);

        } else {

            throw new JsonSerializationException(
                    "Unsupported number type: "
                            + value.getClass().getName());
        }
    }

    // =====================================
    // Serialize Boolean
    // =====================================

    public void serializerBoolean() {
        throw new UnsupportedOperationException(
                "Use serializer(Object) to serialize a boolean");
    }

    private void serializerBoolean(
            Boolean value,
            StringBuilder output) {

        output.append(value);
    }

    // =====================================
    // Serialize Null
    // =====================================

    public void serializerNull() {
        throw new UnsupportedOperationException(
                "Use serializer(Object) to serialize null");
    }

    private void serializerNull(StringBuilder output) {
        output.append("null");
    }

    // =====================================
    // Circular Reference Validation
    // =====================================

    private void checkCircularReference(
            Object value,
            Set<Object> visited) {

        if (!visited.add(value)) {

            throw new JsonSerializationException(
                    "Circular reference detected: "
                            + value.getClass().getName());
        }
    }
}
