package parser;

public enum DuplicateKeyPolicy {

    // Throw exception when duplicate key is found
    REJECT,

    // Keep the first value
    KEEP_FIRST,

    // Replace with the latest value
    KEEP_LAST
}
