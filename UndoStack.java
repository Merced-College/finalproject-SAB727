import java.util.Stack;

public class UndoStack {
    private Stack<String> stack = new Stack<>();

    public void push(String action) {
        stack.push(action);
    }

    public String pop() {
        if (stack.isEmpty()) return "Nothing to undo";
        return stack.pop();
    }
}
