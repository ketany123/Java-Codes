
package MultiThreading;

/*  Demonstrates a race condition in Java. 
Multiple threads share the same Counter object and increment the same variable concurrently.
Expected result: 2 threads × 1,000 increments = 2,000 
Actual result may be less than 2,000 because 
counter++ is not an atomic operation. */

class Counter {

    int count = 0;

    public void increment() {
        count++;
    }
}

public class RaceCondition {

    public static void main(String[] args) throws InterruptedException {

      // One shared Counter object is accessed by all threads.
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

// Race Condition
// A race condition occurs when multiple threads access and modify shared mutable data concurrently,
// causing the final result to depend on the timing of thread execution.

// Example
// RaceCondition.java creates four threads. Each thread increments the same counter 1,000 times.

