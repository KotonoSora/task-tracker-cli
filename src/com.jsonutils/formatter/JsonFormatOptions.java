package formatter;

import java.util.Objects;

public record JsonFormatOptions(

        int indentSize,

        String indentCharacter,

        String lineSeparator,

        boolean spaceAfterColon,

        boolean spaceAfterComma,

        UnicodePolicy unicodePolicy,

        ArrayStyle arrayStyle,

        boolean emptyContainerInline

) {

    public enum ArrayStyle {
        MULTI_LINE,
        SINGLE_LINE
    }

    public JsonFormatOptions {

        if (indentSize < 0) {
            throw new IllegalArgumentException(
                    "Indent size cannot be negative");
        }

        if (!" ".equals(indentCharacter)
                && !"\t".equals(indentCharacter)) {

            throw new IllegalArgumentException(
                    "Indent character must be space or tab");
        }

        if (!"\n".equals(lineSeparator)
                && !"\r\n".equals(lineSeparator)
                && !"\r".equals(lineSeparator)) {

            throw new IllegalArgumentException(
                    "Unsupported line separator");
        }

        Objects.requireNonNull(
                unicodePolicy,
                "Unicode policy cannot be null");

        Objects.requireNonNull(
                arrayStyle,
                "Array style cannot be null");
    }

    // =====================================
    // Default Options
    // =====================================

    public static JsonFormatOptions defaults() {

        return new JsonFormatOptions(
                4,
                " ",
                "\n",
                true,
                true,
                UnicodePolicy.PRESERVE,
                ArrayStyle.MULTI_LINE,
                true);
    }

    // =====================================
    // Compact Options
    // =====================================

    public static JsonFormatOptions compact() {

        return new JsonFormatOptions(
                0,
                " ",
                "\n",
                false,
                false,
                UnicodePolicy.PRESERVE,
                ArrayStyle.SINGLE_LINE,
                true);
    }

    // =====================================
    // Builder
    // =====================================

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private int indentSize = 4;

        private String indentCharacter = " ";

        private String lineSeparator = "\n";

        private boolean spaceAfterColon = true;

        private boolean spaceAfterComma = true;

        private UnicodePolicy unicodePolicy = UnicodePolicy.PRESERVE;

        private ArrayStyle arrayStyle = ArrayStyle.MULTI_LINE;

        private boolean emptyContainerInline = true;

        private Builder() {
        }

        public Builder indentSize(int value) {
            this.indentSize = value;
            return this;
        }

        public Builder indentCharacter(String value) {
            this.indentCharacter = value;
            return this;
        }

        public Builder lineSeparator(String value) {
            this.lineSeparator = value;
            return this;
        }

        public Builder spaceAfterColon(boolean value) {
            this.spaceAfterColon = value;
            return this;
        }

        public Builder spaceAfterComma(boolean value) {
            this.spaceAfterComma = value;
            return this;
        }

        public Builder unicodePolicy(UnicodePolicy value) {
            this.unicodePolicy = value;
            return this;
        }

        public Builder arrayStyle(ArrayStyle value) {
            this.arrayStyle = value;
            return this;
        }

        public Builder emptyContainerInline(boolean value) {
            this.emptyContainerInline = value;
            return this;
        }

        public JsonFormatOptions build() {

            return new JsonFormatOptions(
                    indentSize,
                    indentCharacter,
                    lineSeparator,
                    spaceAfterColon,
                    spaceAfterComma,
                    unicodePolicy,
                    arrayStyle,
                    emptyContainerInline);
        }
    }
}
