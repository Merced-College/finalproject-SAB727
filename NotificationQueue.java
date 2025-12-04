import java.util.LinkedList;
import java.util.Queue;

public class NotificationQueue {
    private Queue<String> notifications = new LinkedList<>();

    public void addNotification(String message) {
        notifications.add(message);
    }

    public String getNextNotification() {
        return notifications.poll();
    }
}
