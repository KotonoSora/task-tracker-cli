package path;

public final class JsonPath {

    private final JsonPathGetter getter;
    private final JsonPathSetter setter;
    private final JsonPathRemove remove;
    private final JsonPathHas has;

    public JsonPath(Object source) {
        this.getter = new JsonPathGetter(source);
        this.setter = new JsonPathSetter(source);
        this.remove = new JsonPathRemove(source);
        this.has = new JsonPathHas(source);
    }

    public JsonPathGetter getter() {
        return getter;
    }

    public JsonPathSetter setter() {
        return setter;
    }

    public JsonPathRemove remove() {
        return remove;
    }

    public JsonPathHas has() {
        return has;
    }
}