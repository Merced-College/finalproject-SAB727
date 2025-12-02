import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

public class TaskManager {
    private static final Path TASK_FILE = Paths.get("tasks.txt");

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String cmd = args[0].toLowerCase();
        try {
            switch (cmd) {
                case "add":
                    addTask(joinArgs(args, 1));
                    break;
                case "list":
                    listTasks();
                    break;
                case "remove":
                    removeTaskArg(args);
                    break;
                case "clear":
                    clearTasks();
                    break;
                default:
                    System.err.println("Unknown command: " + cmd);
                    printUsage();
            }
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format: " + e.getMessage());
        }
    }

    private static void printUsage() {
        System.out.println("TaskManager usage:");
        System.out.println("  java TaskManager add <task description>   - add a task");
        System.out.println("  java TaskManager list                     - list tasks");
        System.out.println("  java TaskManager remove <index>           - remove task by 1-based index");
        System.out.println("  java TaskManager clear                    - remove all tasks");
    }

    private static String joinArgs(String[] args, int start) {
        if (args.length <= start) return "";
        return String.join(" ", java.util.Arrays.copyOfRange(args, start, args.length)).trim();
    }

    private static void addTask(String task) throws IOException {
        if (task.isEmpty()) {
            System.err.println("No task provided to add.");
            return;
        }
        Files.createDirectories(TASK_FILE.getParent() == null ? Paths.get(".") : TASK_FILE.getParent());
        Files.write(TASK_FILE, (task + System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        System.out.println("Added: " + task);
    }

    private static void listTasks() throws IOException {
        if (!Files.exists(TASK_FILE)) {
            System.out.println("No tasks found.");
            return;
        }
        List<String> lines = Files.readAllLines(TASK_FILE);
        if (lines.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }
        for (int i = 0; i < lines.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, lines.get(i));
        }
    }

    private static void removeTaskArg(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Please provide the index to remove.");
            return;
        }
        int idx = Integer.parseInt(args[1]);
        removeTask(idx);
    }

    private static void removeTask(int oneBasedIndex) throws IOException {
        if (!Files.exists(TASK_FILE)) {
            System.err.println("No tasks to remove.");
            return;
        }
        List<String> lines = Files.readAllLines(TASK_FILE);
        int idx = oneBasedIndex - 1;
        if (idx < 0 || idx >= lines.size()) {
            System.err.println("Index out of range.");
            return;
        }
        String removed = lines.remove(idx);
        Files.write(TASK_FILE, lines, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        System.out.println("Removed: " + removed);
    }

    private static void clearTasks() throws IOException {
        if (Files.exists(TASK_FILE)) {
            Files.delete(TASK_FILE);
        }
        System.out.println("All tasks cleared.");
    }
}