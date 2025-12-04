import java.util.HashMap;

public class TaskLookupTable {
    private HashMap<Integer, Task> taskMap = new HashMap<>();

    public void addTask(Task task) {
        taskMap.put(task.getId(), task);
    }

    public Task getTask(int id) {
        return taskMap.get(id);
    }

    public void removeTask(int id) {
        taskMap.remove(id);
    }
}
