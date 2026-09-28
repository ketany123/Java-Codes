package MultiThreading;

class Counter {
    int count = 0;

  /* Synchronized block: Only the critical section is protected. This gives more control over what code needs the lock. */
    public synchronized void increment() {
        count++;
    }
}

public class Synchronization {

    public static void main(String[] args) throws InterruptedException {

        Counter counter = new Counter();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        });

        t1.start();
        t2.start();

        // Wait for both threads to finish
        t1.join();
        t2.join();

        System.out.println("Expected: 2000");
        System.out.println("Actual: " + counter.count);
    }
}


// Synchronization prevents multiple threads from executing a critical section simultaneously when they share the same lock.

// Synchronization.java demonstrates two approaches:

// 1. Synchronized Method
// public synchronized void increment() {
//     count++;
// }
// The object's lock is acquired before entering the method and released when the method finishes.

// 2. Synchronized Block
// synchronized (this) {
//     count++;
// }
// Only the code inside the block is protected.

// Key Difference
// synchronized method
//         ↓
// entire method is protected

// synchronized block
//         ↓
// only selected code is protected



