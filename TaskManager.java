import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

record Task(int id, String description) {}

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

    // Load tasks from file into multiple in-memory data structures:
    // - LinkedList<Task> as the primary ordered container
    // - HashMap<Integer, Task> for id -> task lookup (hash table)
    // - Stack<Task> and Queue<Task> to demonstrate LIFO/FIFO containers
    // - Task[] to demonstrate array usage
    private static LinkedList<Task> loadTasks() throws IOException {
        LinkedList<Task> list = new LinkedList<>();
        if (!Files.exists(TASK_FILE)) return list;
        List<String> lines = Files.readAllLines(TASK_FILE);
        for (String line : lines) {
            if (line.isBlank()) continue;
            int sep = line.indexOf(':');
            if (sep <= 0) continue;
            try {
                int id = Integer.parseInt(line.substring(0, sep));
                String desc = line.substring(sep + 1);
                list.add(new Task(id, desc));
            } catch (NumberFormatException ignored) {
            }
        }
        return list;
    }

    private static void writeTasks(Collection<Task> tasks) throws IOException {
        Files.createDirectories(TASK_FILE.getParent() == null ? Paths.get(".") : TASK_FILE.getParent());
        List<String> out = new ArrayList<>();
        for (Task t : tasks) {
            out.add(t.id() + ":" + t.description());
        }
        Files.write(TASK_FILE, out, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private static void addTask(String task) throws IOException {
        if (task.isEmpty()) {
            System.err.println("No task provided to add.");
            return;
        }
        LinkedList<Task> tasks = loadTasks();
        // Determine next id
        int nextId = tasks.stream().mapToInt(Task::id).max().orElse(0) + 1;
        Task t = new Task(nextId, task);
        // Demonstrate use of different structures
        tasks.add(t); // LinkedList
        // Array
        Task[] arr = tasks.toArray(new Task[0]);
        // Build hash table (id -> task) from the array
        HashMap<Integer, Task> map = new HashMap<>();
        for (Task x : arr) map.put(x.id(), x);
        // Stack (LIFO) - push all tasks to demonstrate
        Stack<Task> stack = new Stack<>();
        for (Task x : arr) stack.push(x);
        // Queue (FIFO) - create from array and call peek to demonstrate usage
        new ArrayDeque<>(Arrays.asList(arr)).peek();

        // Persist
        writeTasks(tasks);
        System.out.println("Added: " + t.description() + " (total: " + tasks.size() + ")");
    }

    private static void listTasks() throws IOException {
        LinkedList<Task> tasks = loadTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks found.");
            return;
        }
        // Demonstrate conversion to array and iteration
        Task[] arr = tasks.toArray(new Task[0]);
        for (int i = 0; i < arr.length; i++) {
            System.out.printf("%d. %s%n", i + 1, arr[i].description());
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
        LinkedList<Task> tasks = loadTasks();
        if (tasks.isEmpty()) {
            System.err.println("No tasks to remove.");
            return;
        }
        int idx = oneBasedIndex - 1;
        if (idx < 0 || idx >= tasks.size()) {
            System.err.println("Index out of range.");
            return;
        }
        Task removed = tasks.remove(idx);
        writeTasks(tasks);
        System.out.println("Removed: " + removed.description());
    }

    private static void clearTasks() throws IOException {
        if (Files.exists(TASK_FILE)) {
            Files.delete(TASK_FILE);
        }
        System.out.println("All tasks cleared.");
    }
}