# Task Tracker CLI

A command-line task tracker written in Java, following the [Roadmap.sh Task Tracker CLI](https://roadmap.sh/projects/task-tracker) project.

## Requirements

- Java 25+
- Bash (Linux, macOS, or Git Bash on Windows)

## Tech Stack

- Java 25+
- Java modules (`module-info.java`)
- Custom JSON library (`com.jsonutils`), no external dependencies

## Folder Structure

```
task-tracker-cli/
├── data/
│   └── tasks.json              # Sample task data
├── dist/
│   └── task-cli.jar            # Build output (created by build.sh)
├── scripts/
│   ├── build.sh                # Compile source and package into dist/task-cli.jar
│   └── run.sh                  # Run dist/task-cli.jar
└── src/
    ├── com.app/                # Entry point (MainApp)
    ├── com.cli/                # Command-line interface (TaskCli)
    ├── com.jsonutils/          # JSON parser, serializer, JsonPath, utilities
    └── com.tasktracker/        # Task model, services, and storage
```

## Build

```bash
./scripts/build.sh
```

This compiles all modules into `out/` and packages the application into `dist/task-cli.jar`.

## Run

```bash
./scripts/run.sh <command> [arguments]
```

Or run the jar directly:

```bash
java -jar dist/task-cli.jar <command> [arguments]
```

## Usage

```bash
# Add a task
task-cli add "Buy groceries"
# Task added successfully (ID: 1)

# Update and delete
task-cli update 1 "Buy groceries and cook dinner"
task-cli delete 1

# Change status
task-cli mark-in-progress 1
task-cli mark-done 1

# List all tasks
task-cli list

# List by status
task-cli list todo
task-cli list in-progress
task-cli list done
```

Tasks are stored in `data/tasks.json`.

## Configure an Alias

Add this line to `~/.bashrc` or `~/.zshrc`, replacing the path with your project location:

```bash
alias task-cli='$HOME/projects/task-tracker-cli/scripts/run.sh'
```

Then reload your shell:

```bash
source ~/.bashrc   # or source ~/.zshrc
```

## License

This project is licensed under the MIT License.