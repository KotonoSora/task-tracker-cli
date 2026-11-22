package formatter;

public enum UnicodePolicy {

    /**
     * Preserve Unicode characters.
     */
    PRESERVE,

    /**
     * Escape non-ASCII characters.
     */
    ESCAPE_NON_ASCII,

    /**
     * Escape all characters inside JSON strings.
     */
    ESCAPE_ALL
}
