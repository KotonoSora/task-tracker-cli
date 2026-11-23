package path;

public final class JsonPath {

    private final JsonPathGetter getter;
    private final JsonPathSetter setter;

    public JsonPath(Object source) {
        this.getter = new JsonPathGetter(source);
        this.setter = new JsonPathSetter(source);
    }

    public JsonPathGetter getter() {
        return getter;
    }

    public JsonPathSetter setter() {
        return setter;
    }
}