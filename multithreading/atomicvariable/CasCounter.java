import java.util.concurrent.atomic.AtomicInteger;

public class CasCounter {

    private final AtomicInteger counter = new AtomicInteger(0);

    /**
     * Increments the counter using Compare-And-Set (CAS).
     *
     * CAS follows this pattern:
     *
     * READ -> CALCULATE -> CAS -> SUCCESS / RETRY
     */
    public void increment() {

        while (true) {

            // Read the current value
            int current = counter.get();

            // Calculate the new value
            int next = current + 1;

            // Try to change current -> next
            if (counter.compareAndSet(current, next)) {
                return;
            }

            /*
             * CAS failed.
             *
             * This means another thread changed the counter
             * after we read the value.
             *
             * Loop again and read the latest value.
             */
        }
    }

    public int getCounter() {
        return counter.get();
    }

    public static void main(String[] args) throws InterruptedException {

        CasCounter casCounter = new CasCounter();

        Thread[] threads = new Thread[10];

        // Create 10 threads
        for (int i = 0; i < threads.length; i++) {

            threads[i] = new Thread(() -> {

                // Each thread increments 1000 times
                for (int j = 0; j < 1000; j++) {
                    casCounter.increment();
                }
            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Final counter: " + casCounter.getCounter());
    }
}
