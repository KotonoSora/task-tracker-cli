package parser;

public class JsonParser {
    private final String json;
    private int index = 0;

    public JsonParser(String json) {
        this.json = json;
    }

    private boolean hasNext() {
        return this.index < json.length();
    }

    private void skipWhitespace() {
        while (hasNext() && Character.isWhitespace(this.json.charAt(index))) {
            this.index++;
        }
    }

    private Character consume() {
        Character c;

        if (hasNext()) {
            c = this.json.charAt(this.index);
            this.index++;
        } else {
            throw new IllegalStateException();
        }

        return c;
    }

    private void expect(Character args) {
        Character c;

        if (hasNext()) {
            c = this.json.charAt(this.index);
            if (c.equals(args)) {
                this.index++;
            } else {
                throw new IllegalStateException();
            }

        } else {
            throw new IllegalStateException();
        }
    }

    // TODO: parse function
    public String parse() {
        return "TODO: parse function";
    }

    // TODO: parseObject function
    public String parseObject() {
        return "TODO: parseObject function";
    }

    // TODO: parseArray function
    public String parseArray() {
        return "TODO: parseArray function";
    }

    // TODO: parseString function
    public String parseString() {
        return "TODO: parseString function";
    }

    // TODO: parseNumber function
    public String parseNumber() {
        return "TODO: parseNumber function";
    }
}