package object;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class JsonObject {

    private final Map<String, Object> object;

    // =====================================
    // Constructors
    // =====================================

    public JsonObject() {
        this.object = new LinkedHashMap<>();
    }

    public JsonObject(Map<String, Object> source) {

        if (source == null) {
            throw new IllegalArgumentException(
                    "Source object cannot be null");
        }

        this.object = new LinkedHashMap<>(source);
    }

    // =====================================
    // Create object
    // =====================================

    public static JsonObject createObject() {
        return new JsonObject();
    }

    public static JsonObject createObject(
            Map<String, Object> source) {
        return new JsonObject(source);
    }

    // =====================================
    // Get
    // =====================================

    public Object get(String key) {

        validateKey(key);

        return object.get(key);
    }

    // =====================================
    // Put
    // =====================================

    public void put(String key, Object value) {

        validateKey(key);

        object.put(key, value);
    }

    // =====================================
    // Remove
    // =====================================

    public Object remove(String key) {

        validateKey(key);

        return object.remove(key);
    }

    // =====================================
    // Contains key
    // =====================================

    public boolean containsKey(String key) {

        validateKey(key);

        return object.containsKey(key);
    }

    // =====================================
    // Keys
    // =====================================

    public Set<String> keys() {

        return Collections.unmodifiableSet(
                object.keySet());
    }

    // =====================================
    // Values
    // =====================================

    public Collection<Object> values() {

        return Collections.unmodifiableCollection(
                object.values());
    }

    // =====================================
    // Additional operations
    // =====================================

    public int size() {
        return object.size();
    }

    public boolean isEmpty() {
        return object.isEmpty();
    }

    public void clear() {
        object.clear();
    }

    // =====================================
    // Convert to Map
    // =====================================

    public Map<String, Object> toMap() {

        return new LinkedHashMap<>(object);
    }

    public Map<String, Object> asMap() {

        return Collections.unmodifiableMap(object);
    }

    // =====================================
    // Key validation
    // =====================================

    private void validateKey(String key) {

        if (key == null) {
            throw new IllegalArgumentException(
                    "JSON object key cannot be null");
        }
    }

    // =====================================
    // To String
    // =====================================

    @Override
    public String toString() {
        return object.toString();
    }
}
