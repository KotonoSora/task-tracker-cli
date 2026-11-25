package models;

public enum TaskStatus {
    TODO(0, "todo"),
    IN_PROGRESS(1, "in-progress"),
    DONE(2, "done");

    private final int code;
    private final String cliName;

    TaskStatus(int code, String cliName) {
        this.code = code;
        this.cliName = cliName;
    }

    public int code() {
        return code;
    }

    public String cliName() {
        return cliName;
    }

    public static TaskStatus fromCode(int code) {
        for (TaskStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown status code: " + code);
    }

    public static TaskStatus fromCliName(String name) {
        for (TaskStatus status : values()) {
            if (status.cliName.equals(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException(
                "Unknown status: " + name + " (use todo, in-progress, done)");
    }
}