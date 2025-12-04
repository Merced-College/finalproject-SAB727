import java.util.LinkedList;

public class TaskList {
    private LinkedList<Task> tasks = new LinkedList<>();

    public void add(Task task) {
        tasks.add(task);
    }

    public Task remove(int index) {
        return tasks.remove(index);
    }

    public Task get(int index) {
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    // Recursive printing
    public void printRecursive (int index) {
        if (index >= tasks.size()) return;
        System.out.println((index + 1) + ". " + tasks.get(index).toString());
        printRecursive(index + 1);
    }

    public void printAll() {
        printRecursive(0);
    }
}
