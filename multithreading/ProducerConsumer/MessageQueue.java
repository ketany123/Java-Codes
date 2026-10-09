```java
import java.util.LinkedList;
import java.util.Queue;

public class MessageQueue {

    private final Queue<String> queue = new LinkedList<>();
    private final int capacity;

    public MessageQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                    "Capacity must be greater than zero"
            );
        }

        this.capacity = capacity;
    }

    // Producer adds a message to the queue
    public synchronized void put(String message)
            throws InterruptedException {

        // Wait while the queue is full
        while (queue.size() >= capacity) {
            wait();
        }

        queue.add(message);

        System.out.println(
                Thread.currentThread().getName()
                + " produced: " + message
                + " | Queue: " + queue
        );

        // Notify waiting producers and consumers
        notifyAll();
    }

    // Consumer removes a message from the queue
    public synchronized String take()
            throws InterruptedException {

        // Wait while the queue is empty
        while (queue.isEmpty()) {
            wait();
        }

        String message = queue.poll();

        System.out.println(
                Thread.currentThread().getName()
                + " consumed: " + message
                + " | Queue: " + queue
        );

        // Notify waiting producers and consumers
        notifyAll();

        return message;
    }
}
```
