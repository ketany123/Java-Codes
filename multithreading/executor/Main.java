```java
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) {
        ExecutorService executor =
                Executors.newFixedThreadPool(3);

        ScheduledExecutorService scheduler =
                Executors.newScheduledThreadPool(1);

        try {
            // 1. Submit a Runnable task
            executor.submit(() ->
                    System.out.println(
                            "Runnable executed by "
                                    + Thread.currentThread().getName()
                    )
            );

            // 2. Submit a Callable task and retrieve its result
            Future<Integer> future = executor.submit(() -> {
                System.out.println("Calculating result...");
                Thread.sleep(1000);
                return 100;
            });

            try {
                System.out.println("Callable result: " + future.get());
            } catch (ExecutionException e) {
                System.err.println(
                        "Callable failed: " + e.getCause()
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Main thread interrupted");
            }

            // 3. Demonstrate CompletableFuture
            CompletableFuture
                    .supplyAsync(() -> 200)
                    .thenApply(value -> value * 2)
                    .thenAccept(value ->
                            System.out.println(
                                    "CompletableFuture result: " + value
                            )
                    )
                    .join();

            // 4. Demonstrate scheduled execution
            ScheduledFuture<?> scheduledTask =
                    scheduler.schedule(
                            () -> System.out.println(
                                    "Delayed task executed"
                            ),
                            2,
                            TimeUnit.SECONDS
                    );

            try {
                scheduledTask.get();
            } catch (ExecutionException e) {
                System.err.println(
                        "Scheduled task failed: " + e.getCause()
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println(
                        "Interrupted while waiting for scheduled task"
                );
            }

        } finally {
            // 5. Gracefully shut down both executors
            shutdownExecutor(executor);
            shutdownExecutor(scheduler);
        }
    }

    private static void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();

        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();

                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println(
                            "Executor did not terminate"
                    );
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
```
