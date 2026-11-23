package array;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JsonArray {

    private final List<Object> array;

    // =====================================
    // Constructors
    // =====================================

    public JsonArray() {
        this.array = new ArrayList<>();
    }

    public JsonArray(Collection<?> source) {

        if (source == null) {
            throw new IllegalArgumentException(
                    "Source array cannot be null");
        }

        this.array = new ArrayList<>(source);
    }

    // =====================================
    // Create Array
    // =====================================

    public static JsonArray createArray() {
        return new JsonArray();
    }

    public static JsonArray createArray(
            Collection<?> source) {

        return new JsonArray(source);
    }

    // =====================================
    // Get
    // =====================================

    public Object get(int index) {

        validateIndex(index);

        return array.get(index);
    }

    // =====================================
    // Add
    // =====================================

    public void add(Object value) {
        array.add(value);
    }

    public void add(int index, Object value) {

        validateInsertIndex(index);

        array.add(index, value);
    }

    // =====================================
    // Set
    // =====================================

    public Object set(int index, Object value) {

        validateIndex(index);

        return array.set(index, value);
    }

    // =====================================
    // Remove
    // =====================================

    public Object remove(int index) {

        validateIndex(index);

        return array.remove(index);
    }

    public boolean remove(Object value) {
        return array.remove(value);
    }

    // =====================================
    // Size
    // =====================================

    public int size() {
        return array.size();
    }

    // =====================================
    // Is Empty
    // =====================================

    public boolean isEmpty() {
        return array.isEmpty();
    }

    // =====================================
    // Additional Operations
    // =====================================

    public void clear() {
        array.clear();
    }

    public boolean contains(Object value) {
        return array.contains(value);
    }

    public int indexOf(Object value) {
        return array.indexOf(value);
    }

    public int lastIndexOf(Object value) {
        return array.lastIndexOf(value);
    }

    // =====================================
    // Values
    // =====================================

    public List<Object> values() {

        return Collections.unmodifiableList(array);
    }

    // =====================================
    // Convert To List
    // =====================================

    public List<Object> toList() {

        return new ArrayList<>(array);
    }

    public List<Object> asList() {

        return Collections.unmodifiableList(array);
    }

    // =====================================
    // Index Validation
    // =====================================

    private void validateIndex(int index) {

        if (index < 0 || index >= array.size()) {

            throw new IndexOutOfBoundsException(
                    "Array index out of bounds: " + index
                            + ", size: " + array.size());
        }
    }

    // =====================================
    // Insert Index Validation
    // =====================================

    private void validateInsertIndex(int index) {

        if (index < 0 || index > array.size()) {

            throw new IndexOutOfBoundsException(
                    "Array insertion index out of bounds: "
                            + index
                            + ", size: " + array.size());
        }
    }

    // =====================================
    // To String
    // =====================================

    @Override
    public String toString() {
        return array.toString();
    }
}
