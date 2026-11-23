package utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public final class JsonUtils {

    private JsonUtils() {
        throw new UnsupportedOperationException(
                "Utility class cannot be instantiated");
    }

    // =====================================
    // Read JSON File
    // =====================================

    public static String read(String filePath)
            throws IOException {

        Objects.requireNonNull(
                filePath,
                "File path cannot be null");

        return read(Path.of(filePath));
    }

    public static String read(Path path)
            throws IOException {

        Objects.requireNonNull(
                path,
                "File path cannot be null");

        if (!Files.exists(path)) {
            throw new IOException(
                    "JSON file does not exist: " + path);
        }

        if (!Files.isRegularFile(path)) {
            throw new IOException(
                    "Path is not a regular file: " + path);
        }

        return Files.readString(
                path,
                StandardCharsets.UTF_8);
    }

    // =====================================
    // Write JSON File
    // =====================================

    public static void write(
            String filePath,
            String content)
            throws IOException {

        Objects.requireNonNull(
                filePath,
                "File path cannot be null");

        write(Path.of(filePath), content);
    }

    public static void write(
            Path path,
            String content)
            throws IOException {

        Objects.requireNonNull(
                path,
                "File path cannot be null");

        Objects.requireNonNull(
                content,
                "JSON content cannot be null");

        Path absolutePath = path.toAbsolutePath();

        Path parent = absolutePath.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        Files.writeString(
                absolutePath,
                content,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE);
    }

}
