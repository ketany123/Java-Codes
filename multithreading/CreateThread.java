package MultiThreading;

// Runnable implementation for printing letters
class Letters implements Runnable {

    @Override
    public void run() {
        for (int i = 1; i <= 25; i++) {

            System.out.println((char) ('A' + i - 1));

            try {
                // pause the current thread for 500 milliseconds
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Letters thread was interrupted.");
            }
        }
    }
}

// Thread implementation for printing numbers
class Numbers extends Thread {

    @Override
    public void run() {
        for (int i = 1; i <= 25; i++) {

            System.out.println(i);

            try {
                // Pause the current thread for 1 second
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Numbers thread was interrupted.");
            }
        }
    }
}

public class CreateThread {

    public static void main(String[] args) throws InterruptedException {

        // Create a thread using Runnable
        Letters letters = new Letters();
        Thread letterThread = new Thread(letters);

        // Create a thread by extending Thread
        Numbers numberThread = new Numbers();

        // Start both threads concurrently
        letterThread.start();
        numberThread.start();

        /*
         * join() makes the main thread wait until
         * the specified thread has completed.
         */
        letterThread.join();
        numberThread.join();

        // This executes only after both threads finish
        System.out.println("Main thread finished.");
    }
}



// start() creates a new thread and invokes run() on that thread.
// Thread.sleep() pauses the currently executing thread.
// join() makes the calling thread wait until another thread completes.
