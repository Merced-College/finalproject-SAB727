import java.util.*;

public class TaskManager {
    private final TaskList taskList = new TaskList();
    private final UndoStack undoStack = new UndoStack();

    private int nextId = 1;
    private final Scanner scanner = new Scanner(System.in);

    // Array requirement (list of valid commands)
    private final String[] validCommands = {"add", "list", "complete", "incomplete", "delete", "edit", "exit"};

    public static void main(String[] args) {
        new TaskManager().start();
    }

    public void start() {
        while (true) {
            System.out.print("\nEnter command (add, list, complete, incomplete, delete, edit, exit): ");
            String command = scanner.nextLine().trim().toLowerCase();

            if (!isValidCommand(command)) {
                System.out.println("Invalid command. Please try again.");
                continue;
            }

            switch (command) {
                case "add":
                    addTask();
                    break;
                case "list":
                    listTasks();
                    break;
                case "complete":
                    completeTask();
                    break;
                case "incomplete":
                    undoTaskCompletion();
                    break;
                case "delete":
                    deleteTask();
                    break;
                case "edit":
                    editTask();
                    break;    
                case "exit":
                    System.out.println("Exiting Task Manager. Goodbye!");
                    return;
            }
        }
    }

    private boolean isValidCommand(String command) {
        for (String c : validCommands) {
            if (c.equals(command)) return true;
        }
        return false;
    }

    private void addTask() {
        System.out.print("Enter description: ");
        String text = scanner.nextLine().trim();
        if (text.isEmpty()) {
            System.out.println("Empty description, task not added.");
            return;
        }

        Task t = new Task(nextId++, text);

        taskList.add(t);

        System.out.println("Task added.");
    }

    private void completeTask() {
        listTasks();
        System.out.print("Enter number to complete: ");
        int index = readIndexFromUser();
        if (index < 0) return;

        Task t = taskList.get(index);
        if (t == null) {
            System.out.println("No task at that number.");
            return;
        }
        t.markCompleted();

        System.out.println("Task marked as completed.");
    }

    private void deleteTask() {
        listTasks();
        System.out.print("Enter number to delete: ");
        int index = readIndexFromUser();
        if (index < 0) return;

        Task removed = taskList.remove(index);
        if (removed == null) {
            System.out.println("No task at that number.");
            return;
        }

        System.out.println("Task deleted.");
    }

    private void listTasks() {
        taskList.printAll();
    }

    private int readIndexFromUser() {
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx < 0) {
                System.out.println("Invalid number.");
                return -1;
            }
            return idx;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return -1;
        }
    }

    // --- Helper classes ---
    private static class Task {
        private final int id;
        private String description;
        private boolean completed = false;

        Task(int id, String description) {
            this.id = id;
            this.description = description;
        }

        int getId() { return id; }
        String getDescription() { return description; }
        void setDescription(String description) { this.description = description; }
        void markCompleted() { completed = true; }
        boolean isCompleted() { return completed; }

        @Override
        public String toString() {
            return id + ". [" + (completed ? "x" : " ") + "] " + description;
        }
    }

    private static class TaskList {
        private final LinkedList<Task> list = new LinkedList<>();

        void add(Task t) { list.add(t); }
        Task get(int index) {
            if (index < 0 || index >= list.size()) return null;
            return list.get(index);
        }
        Task remove(int index) {
            if (index < 0 || index >= list.size()) return null;
            return list.remove(index);
        }
        void printAll() {
            if (list.isEmpty()) {
                System.out.println("No tasks.");
                return;
            }
            for (int i = 0; i < list.size(); i++) {
                Task t = list.get(i);
                System.out.printf("%d. [%s] %s%n", i+1, t.isCompleted() ? "x" : " ", t.getDescription());
            }
        }
    }

    private static class UndoStack {
        private final Stack<String> stack = new Stack<>();
        void push(String s) { stack.push(s); }
        String pop() { return stack.isEmpty() ? null : stack.pop(); }
    }

    // I've written the editTask method below
    private void editTask() {
        listTasks(); // show current tasks
        System.out.print("Enter number to edit: "); // asks user for task number
        int index = readIndexFromUser(); // checks is task number is valid
        if (index < 0) return; // stops method if invalid

        Task t = taskList.get(index); // gets the task at that index
        if (t == null) { // checks if task exists
            System.out.println("No task at that number.");
            return;
        }

        System.out.print("Enter new description: "); // asks for new description
        String newDescription = scanner.nextLine().trim(); // reads new description and trims whitespace
        if (newDescription.isEmpty()) { // checks if new description is empty
            System.out.println("Empty description, task not edited.");
            return;
        }

        t.setDescription(newDescription); // sets new description

        System.out.println("Task description updated."); // confirms update
    }

    // Undo completion of a task
    private void undoTaskCompletion() {
        listTasks(); // show all tasks
        System.out.print("Enter number to undo completion: "); // asks user for task number
        int index = readIndexFromUser(); // checks if task number is valid
        if (index < 0) return; // stops method if invalid

        Task t = taskList.get(index); // gets the task at that index
        if (t == null) { // checks if task exists
            System.out.println("No task at that number.");
            return;
        }

        if (!t.isCompleted()) { // checks if task is already incomplete
            System.out.println("Task is not completed.");
            return;
        }
            
        // Undo completion
        t.completed = false; // mark task as incomplete
        System.out.println("Task marked as incomplete."); // confirms update
    }
}