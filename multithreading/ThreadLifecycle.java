package multithreading;

/*
 * Demonstrates the lifecycle of a Java thread.
 *
 * Thread states covered:
 * 1. NEW           - Thread is created but not started.
 * 2. RUNNABLE      - Thread is ready to run / running.
 * 3. TIMED_WAITING - Thread is sleeping for a specific amount of time.
 * 4. TERMINATED    - Thread has completed execution.
 */

class WorkerThread extends Thread {

    @Override
    public void run() {
        System.out.println("Worker thread started.");
        try {
            // The current thread enters TIMED_WAITING state.
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // Restore the interrupted status of the thread.
            Thread.currentThread().interrupt();
            System.out.println("Worker thread was interrupted.");
        }
        System.out.println("Worker thread finished.");
    }
}

public class ThreadLifecycle {

    public static void main(String[] args) throws InterruptedException {

        // Create the worker thread.
        WorkerThread worker = new WorkerThread();

        // Thread is created but has not been started yet.
        System.out.println("Before start: " + worker.getState());

        // Starts a new thread and executes the run() method.
        worker.start();

        /*
         * The exact state here may vary because of thread scheduling.
         * It can be RUNNABLE or TIMED_WAITING.
         */
        System.out.println("After start: " + worker.getState());

        // Give the worker time to enter the sleep state.
        Thread.sleep(500);

        // Worker should be in TIMED_WAITING while sleeping.
        System.out.println("While worker is sleeping: " + worker.getState());

        /*
         * join() makes the main thread wait until
         * the worker thread finishes.
         */
        worker.join();

        // Once run() finishes, the thread becomes TERMINATED.
        System.out.println("After join: " + worker.getState());

        System.out.println("Main thread finished.");
    }
}




// Thread States Demonstrated
// 1. NEW
// The thread has been created but has not been started yet.
// WorkerThread worker = new WorkerThread();

// 2. RUNNABLE
// After calling:
// worker.start();
// the thread becomes eligible to run.
// The exact state observed immediately after start() can vary because thread scheduling is controlled by the JVM/OS.

// 3. TIMED_WAITING
// When the worker executes:
// Thread.sleep(3000);
// it enters the TIMED_WAITING state for approximately 3 seconds.

// 4. TERMINATED
// After the run() method finishes, the thread enters the TERMINATED state.
// join()
// The main thread uses:
// worker.join();
// This makes the main thread wait until the worker thread completes.
// Therefore, this:
// After join: TERMINATED
// is printed only after the worker has finished.
