# Java Lock & ReentrantLock

## 1. What Is a Lock?

A **Lock** is a synchronization mechanism used to control access to shared resources.

Suppose multiple threads access the same data:

```text
Thread A ──┐
Thread B ──┼──> Shared Resource
Thread C ──┘
```

Without synchronization, multiple threads may modify the resource simultaneously, potentially causing race conditions.

A lock allows us to control access to a **critical section** so that only one thread holding that lock can execute it at a time.

### Critical Section

```text
Thread A
   |
   | Acquire lock
   v
+-------------------+
| Critical Section  |
+-------------------+
   |
   | Release lock
   v
Another thread can acquire the lock
```

## 2. Java Lock Interface

Java provides the `Lock` interface in the `java.util.concurrent.locks` package.

```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
```

One common implementation is `ReentrantLock`.

```java
Lock lock = new ReentrantLock();
```

### Important Methods

| Method                   | Description                                               |
| ------------------------ | --------------------------------------------------------- |
| `lock()`                 | Waits until the lock can be acquired.                     |
| `unlock()`               | Releases one hold of the lock.                            |
| `tryLock()`              | Attempts to acquire the lock without waiting.             |
| `tryLock(timeout, unit)` | Waits up to a specified time to acquire the lock.         |
| `lockInterruptibly()`    | Waits for the lock but allows interruption while waiting. |
| `newCondition()`         | Creates a `Condition` associated with the lock.           |

## 3. `lock()`

```java
lock.lock();
```

Attempts to acquire the lock.

If another thread owns the lock, the current thread waits until it can acquire it.

```text
Thread A
   |
   +-- lock()
   |
   +-- Execute critical section
   |
   +-- unlock()
          |
          v
Thread B can acquire the lock
```

### Standard Pattern

```java
lock.lock();

try {
    // Critical section
} finally {
    lock.unlock();
}
```

## 4. `unlock()`

```java
lock.unlock();
```

Releases one hold of the lock.

It is important to place `unlock()` inside a `finally` block so that the lock is released even if an exception occurs after acquisition.

```java
lock.lock();

try {
    // Perform some operation
} finally {
    lock.unlock();
}
```

**Important:** A thread must own the lock before calling `unlock()`. Otherwise, `IllegalMonitorStateException` is thrown.

## 5. What Does Reentrant Mean?

**Reentrant** means that the same thread can acquire the same lock multiple times without deadlocking itself.

For example:

```java
lock.lock();   // Acquisition #1
lock.lock();   // Acquisition #2

lock.unlock(); // Release #1
lock.unlock(); // Release #2
```

`ReentrantLock` tracks how many times the owning thread has acquired the lock. This is called the **hold count**.

## 6. Understanding the Hold Count

Suppose Thread A executes:

```java
lock.lock();
```

The hold count becomes:

```text
Hold Count = 1
```

Then it calls another method:

```java
updateInventory();
```

Inside that method, the same thread executes:

```java
lock.lock();
```

The hold count becomes:

```text
Hold Count = 2
```

When the first `unlock()` executes:

```java
lock.unlock();
```

The hold count changes:

```text
2 → 1
```

The lock is still held by Thread A.

When the second `unlock()` executes:

```java
lock.unlock();
```

The hold count changes:

```text
1 → 0
```

Now the lock is fully released, and another thread can acquire it.

## 7. Practical Example: Nested Method Locking

Consider an order-processing service in which `processOrder()` calls `updateInventory()`. Both methods acquire the same `ReentrantLock`.

### Complete Code

```java
package MultiThreading;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OrderService {

    private final Lock lock = new ReentrantLock();

    public void processOrder() {

        lock.lock();

        try {
            System.out.println(
                Thread.currentThread().getName()
                    + " - processOrder acquired lock"
            );

            updateInventory();

            System.out.println(
                Thread.currentThread().getName()
                    + " - processOrder finished"
            );

        } finally {
            lock.unlock();
        }
    }

    public void updateInventory() {

        lock.lock();

        try {
            System.out.println(
                Thread.currentThread().getName()
                    + " - updateInventory acquired lock"
            );

            Thread.sleep(1000);

            System.out.println(
                Thread.currentThread().getName()
                    + " - updateInventory finished"
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {

        OrderService service = new OrderService();

        Thread thread = new Thread(
            service::processOrder,
            "Order-Thread"
        );

        thread.start();
    }
}
```

### How the Example Works

**Step 1:** `processOrder()` acquires the lock.

```java
lock.lock();
```

Hold count:

```text
1
```

**Step 2:** `processOrder()` calls `updateInventory()`.

```java
updateInventory();
```

**Step 3:** `updateInventory()` acquires the same lock.

```java
lock.lock();
```

Hold count:

```text
2
```

**Step 4:** `updateInventory()` finishes and releases its hold.

```java
lock.unlock();
```

Hold count:

```text
2 → 1
```

**Step 5:** `processOrder()` finishes and releases its hold.

```java
lock.unlock();
```

Hold count:

```text
1 → 0
```

The lock is now fully released.

### Execution Flow

```text
processOrder()
      |
      | lock.lock()
      v
  Hold Count = 1
      |
      v
updateInventory()
      |
      | lock.lock()
      v
  Hold Count = 2
      |
      | Perform work
      v
    unlock()
      |
      v
  Hold Count = 1
      |
      v
processOrder()
      |
      | unlock()
      v
  Hold Count = 0
      |
      v
  Lock Released
```

### Why Doesn't It Deadlock?

Both lock acquisitions are performed by the **same thread**.

`ReentrantLock` recognizes that the requesting thread already owns the lock, so it allows the thread to acquire it again and increments the hold count.

This is useful when a locked method calls another method that also needs the same lock.

## 8. `ReentrantLock` vs. `synchronized`

Both mechanisms provide mutual exclusion, and both are reentrant.

### Using `synchronized`

```java
public synchronized void processOrder() {
    // Critical section
}
```

Java automatically acquires and releases the object's intrinsic monitor as execution enters and exits the synchronized region.

### Using `ReentrantLock`

```java
lock.lock();

try {
    // Critical section
} finally {
    lock.unlock();
}
```

With `ReentrantLock`, you explicitly control acquisition and release.

### Comparison

| Feature                        | `synchronized`       | `ReentrantLock`       |
| ------------------------------ | -------------------- | --------------------- |
| Mutual exclusion               | Yes                  | Yes                   |
| Reentrant                      | Yes                  | Yes                   |
| Automatic release              | Yes                  | No; use `finally`     |
| Non-blocking acquisition       | No direct equivalent | `tryLock()`           |
| Timed acquisition              | No direct equivalent | Yes                   |
| Interruptible lock acquisition | No direct equivalent | `lockInterruptibly()` |
| Multiple `Condition` objects   | No direct equivalent | Yes                   |
| Fairness option                | No                   | Yes, configurable     |

A fair `ReentrantLock` can be created with:

```java
Lock lock = new ReentrantLock(true);
```

Fairness requests that waiting threads acquire the lock in roughly arrival order. It does not guarantee fair scheduling of threads, and it can reduce throughput.

## 9. `lock()` vs. `tryLock()`

### `lock()`

```java
lock.lock();
```

Waits until the lock becomes available.

```text
Lock is occupied
       |
       v
Thread waits
       |
       v
Lock is released
       |
       v
Thread acquires lock
```

### `tryLock()`

```java
if (lock.tryLock()) {
    try {
        // Critical section
    } finally {
        lock.unlock();
    }
} else {
    System.out.println("Could not acquire lock");
}
```

`tryLock()` attempts to acquire the lock immediately.

If another thread owns it, the method returns `false` rather than waiting.

### `tryLock(timeout, unit)`

```java
import java.util.concurrent.TimeUnit;

try {
    if (lock.tryLock(5, TimeUnit.SECONDS)) {
        try {
            // Critical section
        } finally {
            lock.unlock();
        }
    } else {
        System.out.println("Could not acquire lock within 5 seconds");
    }
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
```

This method waits for up to five seconds to acquire the lock.

* If acquired within the timeout, it returns `true`.
* If the timeout expires first, it returns `false`.
* If interrupted while waiting, it throws `InterruptedException`.

## 10. Important Rule: Match Every Acquisition With a Release

Every successful call to:

```java
lock.lock();
```

must eventually have a corresponding call to:

```java
lock.unlock();
```

For a reentrant lock:

```text
lock()    → Hold Count +1
lock()    → Hold Count +1
unlock()  → Hold Count -1
unlock()  → Hold Count -1
```

### Correct Example

```java
lock.lock();   // Hold count = 1
lock.lock();   // Hold count = 2

lock.unlock(); // Hold count = 1
lock.unlock(); // Hold count = 0
```

### Incorrect Example

```java
lock.lock();
lock.lock();

lock.unlock();
```

The hold count remains `1`, so the lock is still held by that thread. Other threads cannot acquire it until the remaining hold is released.

## 11. Key Takeaways

### `Lock`

* Controls access to critical sections.
* Helps prevent race conditions when shared state is properly protected.
* Requires explicit acquisition and release.

### `ReentrantLock`

* An implementation of the `Lock` interface.
* Allows the same thread to acquire the same lock multiple times.
* Maintains a hold count.
* Supports `tryLock()`, timed acquisition, interruptible acquisition, `Condition`, and optional fairness.

### Mental Model

```text
lock()
   |
   v
Hold Count +1

unlock()
   |
   v
Hold Count -1

Hold Count = 0
   |
   v
Lock Fully Released
```

**Most important rule:** A `ReentrantLock` must be unlocked as many times as it was successfully locked by the owning thread. Always release locks safely, typically inside a `finally` block.
