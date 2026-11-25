package cli;

import java.util.List;

import models.Task;
import models.TaskStatus;
import services.TaskManager;

public final class TaskCli {
    private static final String USAGE = """
            Usage:
                task-cli add "<description>"
                task-cli update <id> "<description>"
                task-cli delete <id>
                task-cli mark-in-progress <id>
                task-cli mark-done <id>
                task-cli list [todo | in-progress | done]""";

    private final TaskManager manager;

    public TaskCli(TaskManager manager) {
        this.manager = manager;
    }

    public void run(String[] args) {
        if (args.length == 0) {
            System.out.println(USAGE);
            return;
        }

        switch (args[0]) {
            case "add" -> {
                Task task = manager.add(argText(args, 1));
                System.out.println(
                        "Task added successfully (ID: " + task.id() + ")");
            }

            case "update" -> {
                Task task = manager.update(argId(args, 1), argText(args, 2));
                System.out.println(
                        "Task updated successfully (ID: " + task.id() + ")");
            }

            case "delete" -> {
                Task task = manager.delete(argId(args, 1));
                System.out.println(
                        "Task deleted successfully (ID: " + task.id() + ")");
            }

            case "mark-in-progress" -> {
                Task task = manager.mark(argId(args, 1), TaskStatus.IN_PROGRESS);
                System.out.println(
                        "Task marked as in-progress (ID: " + task.id() + ")");
            }

            case "mark-done" -> {
                Task task = manager.mark(argId(args, 1), TaskStatus.DONE);
                System.out.println(
                        "Task marked as done (ID: " + task.id() + ")");
            }

            case "list" -> {
                boolean isAll = args.length == 1;

                if (isAll) {
                    printTasks(manager.all());
                } else {
                    printTasks(manager.listWithStatus(
                            TaskStatus.fromCliName(
                                    argText(args, 1))));
                }
            }

            default -> {
                throw new IllegalArgumentException(
                        "Unknow command: " + args[0] + "\n" + USAGE);
            }
        }
    }

    private static String argText(String[] args, int index) {
        if (index >= args.length) {
            throw new IllegalArgumentException("Missing argument\n" + USAGE);
        }
        return args[index];
    }

    private static int argId(String[] args, int index) {
        try {
            return Integer.parseInt(argText(args, index));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID must be a number: " + args[index]);
        }
    }

    private static void printTasks(List<Task> tasks) {
        tasks.forEach(task -> {
            System.out.println("----------------------");
            System.out.println("ID: " + task.id());
            System.out.println("Description: " + task.description());
            System.out.println("Status: " + task.status().cliName());
            System.out.println("Created At: " + task.createdAt());
            System.out.println("Updated At: " + task.updatedAt());
        });
    }
}
