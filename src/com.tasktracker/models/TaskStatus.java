package models;

public enum TaskStatus {
    TODO(0),
    IN_PROGRESS(1),
    DONE(2);

    private final int code;

    TaskStatus(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static TaskStatus fromCode(int code) {
        for (TaskStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status code: " + code);
    }
}