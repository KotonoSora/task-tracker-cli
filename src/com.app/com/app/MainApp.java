package com.app;

import java.io.IOException;
import java.nio.file.Path;

import cli.TaskCli;
import services.TaskManager;
import storage.TaskStorage;

public class MainApp {

    private static final Path DATA_FILE = Path.of("data", "tasks.json");

    public static void main(String[] args) {
        TaskStorage storage = new TaskStorage(DATA_FILE);

        try {
            TaskManager manager = new TaskManager(storage.load());
            new TaskCli(manager).run(args);

            if (isWriteCommand(args)) {
                storage.save(manager.all());
            }

        } catch (IOException e) {
            System.err.println("File error: " + e.getMessage());
            System.exit(1);

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static boolean isWriteCommand(String[] args) {
        return args.length > 0 && !args[0].equals("list");
    }
}