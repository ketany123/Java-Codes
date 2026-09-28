package MultiThreading;

/*
 * Demonstrates the use of the volatile keyword in Java.
 *
 * volatile ensures that changes made by one thread
 * are visible to other threads.
 *
 * In this example:
 * - Worker thread keeps running while 'running' is true.
 * - Main thread changes 'running' to false.
 * - Because 'running' is volatile, the worker thread
 *   can see the updated value and stop.
 */

class Worker extends Thread {

    private volatile boolean running = true;

    @Override
    public void run() {

        while (running) {
            System.out.println("Worker thread is running...");

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Worker thread was interrupted.");
                break;
            }
        }

        System.out.println("Worker thread stopped.");
    }

    public void stopWorker() {
        running = false;
    }
}

public class VolatileExample {

    public static void main(String[] args) throws InterruptedException {

        Worker worker = new Worker();

        worker.start();

        Thread.sleep(2000);

        worker.stopWorker();

        System.out.println("Main thread changed running to false.");

        worker.join();

        System.out.println("Main thread finished.");
    }
}



// volatile is used when multiple threads share a variable and changes made by one thread need to be visible to other threads.

// Important Point->
// volatile does not make compound operations atomic.
// For example:
// volatile int count = 0;
// count++;

// count++ is still not thread-safe because it involves:

// Read the value
// Add 1
// Write the value back

// For operations like this, use synchronization or atomic classes such as AtomicInteger.

