```java
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final int PRODUCER_COUNT = 3;
    private static final int CONSUMER_COUNT = 3;
    private static final int MESSAGES_PER_PRODUCER = 5;

    // Special message telling a consumer to stop
    private static final String POISON_PILL =
            "__STOP_CONSUMER__";

    public static void main(String[] args)
            throws InterruptedException {

        MessageQueue queue = new MessageQueue(2);

        List<Thread> producers = new ArrayList<>();
        List<Thread> consumers = new ArrayList<>();

        // Start consumers first
        for (int i = 1; i <= CONSUMER_COUNT; i++) {
            Thread consumer = new Thread(() -> {
                try {
                    while (true) {
                        String message = queue.take();

                        // Stop when the poison pill is received
                        if (POISON_PILL.equals(message)) {
                            System.out.println(
                                    Thread.currentThread().getName()
                                    + " is stopping."
                            );
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                    System.out.println(
                            Thread.currentThread().getName()
                            + " was interrupted."
                    );
                }
            }, "Consumer-" + i);

            consumers.add(consumer);
            consumer.start();
        }

        // Start producers
        for (int i = 1; i <= PRODUCER_COUNT; i++) {
            final int producerId = i;

            Thread producer = new Thread(() -> {
                try {
                    for (int j = 1;
                         j <= MESSAGES_PER_PRODUCER;
                         j++) {

                        String message =
                                "P" + producerId + "-Message-" + j;

                        queue.put(message);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                    System.out.println(
                            Thread.currentThread().getName()
                            + " was interrupted."
                    );
                }
            }, "Producer-" + i);

            producers.add(producer);
            producer.start();
        }

        // Wait until every producer has finished
        for (Thread producer : producers) {
            producer.join();
        }

        System.out.println(
                "\nAll producers finished. Stopping consumers..."
        );

        // Send one poison pill to each consumer
        for (int i = 0; i < CONSUMER_COUNT; i++) {
            queue.put(POISON_PILL);
        }

        // Wait until every consumer has finished
        for (Thread consumer : consumers) {
            consumer.join();
        }

        System.out.println(
                "\nAll producers and consumers finished."
        );
    }
}
```
