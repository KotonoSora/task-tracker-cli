package path;

public final class JsonPath {

    private final JsonPathGetter getter;

    public JsonPath(Object source) {
        this.getter = new JsonPathGetter(source);
    }

    public JsonPathGetter getter() {
        return getter;
    }
}