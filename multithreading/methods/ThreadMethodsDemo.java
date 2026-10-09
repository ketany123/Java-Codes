```java
public class ThreadMethodsDemo {

    public static void main(String[] args)
            throws InterruptedException {

        // 1. currentThread() and getName()
        System.out.println("Main thread: "
                + Thread.currentThread().getName());

        // 2. Create a thread and set its name
        Thread worker = new Thread(() -> {
            System.out.println("Worker thread: "
                    + Thread.currentThread().getName());

            for (int i = 1; i <= 3; i++) {
                System.out.println("Working: " + i);

                // 3. yield() gives the scheduler a hint
                Thread.yield();
            }
        });

        worker.setName("Worker-1");

        // 4. Check state and alive status before starting
        System.out.println("Before start - State: "
                + worker.getState());

        System.out.println("Before start - Alive: "
                + worker.isAlive());

        // 5. Start the worker
        worker.start();

        System.out.println("After start - State: "
                + worker.getState());

        System.out.println("After start - Alive: "
                + worker.isAlive());

        // 6. Wait for the worker to finish
        worker.join();

        System.out.println("After join - State: "
                + worker.getState());

        System.out.println("After join - Alive: "
                + worker.isAlive());

        // 7. Demonstrate a daemon thread
        Thread daemon = new Thread(() -> {
            System.out.println("Daemon thread running");
        });

        daemon.setName("Background-Worker");
        daemon.setDaemon(true);

        System.out.println("Is daemon? "
                + daemon.isDaemon());

        daemon.start();

        // Wait so the demonstration finishes predictably
        daemon.join();

        System.out.println("Main thread finished");
    }
}
```
