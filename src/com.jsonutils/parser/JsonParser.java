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
        while (hasNext()) {
            char c = this.json.charAt(this.index);
            if (c == 0x0020 || c == 0x0009 || c == 0x000A || c == 0x000D) {
                this.index++;
            } else {
                break;
            }
        }
    }

    private char consume() {
        char c;

        if (hasNext()) {
            c = this.json.charAt(this.index);
            this.index++;
        } else {
            throw new IllegalStateException();
        }

        return c;
    }

    private void expect(char args) {
        char c;

        if (hasNext()) {
            c = this.json.charAt(this.index);
            if (c == args) {
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