# Multiple Producer–Consumer Problem in Java


## 4. How the system works

The shared queue sits between producers and consumers.

```text
Producer 1 ──┐
Producer 2 ──┼──> Shared Queue (capacity = 2) ──┬──> Consumer 1
Producer 3 ──┘                                 ├──> Consumer 2
                                               └──> Consumer 3
```

The queue is shared by all six threads.

If the queue is full, producers wait until a consumer removes a message.

If the queue is empty, consumers wait until a producer adds a message.

## 5. Why do we need synchronization?

Suppose the queue has one free slot and two producers try to add a message simultaneously.

Without synchronization, both producers might observe the same queue state and attempt to modify it concurrently.

This can cause incorrect behavior or violate the capacity limit.

Using `synchronized` ensures that only one thread at a time executes a synchronized method on the same queue object.

```java
public synchronized void put(String message) {
    // Safely modify the shared queue
}
```

The same monitor protects both `put()` and `take()`.

Because both methods synchronize on the same `MessageQueue` instance, producers and consumers coordinate access to the shared queue.

## 6. Understanding `wait()`

The `wait()` method causes the current thread to wait until it is notified, interrupted, or (when using a timed wait) the timeout expires.

When called while holding an object's monitor, `wait()` releases that object's monitor while waiting.

### Producer example

```java
while (queue.size() >= capacity) {
    wait();
}
```

If the queue is full:

1. The producer calls `wait()`.
2. It releases the `MessageQueue` object's monitor.
3. Another thread can enter `take()` and remove a message.
4. The producer can wake and compete to reacquire the monitor.
5. After reacquiring it, the producer checks the condition again.

### Consumer example

```java
while (queue.isEmpty()) {
    wait();
}
```

If the queue is empty, the consumer waits until a message may be available.

### Why must `wait()` be called inside `synchronized` code?

A thread must own the relevant object's monitor before calling `wait()` on that object. Otherwise, Java throws `IllegalMonitorStateException`.

## 7. Why use `while` instead of `if`?

Correct:

```java
while (queue.isEmpty()) {
    wait();
}
```

Unsafe pattern:

```java
if (queue.isEmpty()) {
    wait();
}
```

A waiting thread must recheck the condition after waking.

This is important because:

* Java permits spurious wakeups.
* Another thread might consume the message before this thread reacquires the monitor.
* A notification does not guarantee that the condition is true when the thread resumes.

The same reasoning applies to producers checking whether the queue is full.

**Rule:** Always recheck the condition in a `while` loop around `wait()`.

## 8. Understanding `notifyAll()`

After a producer adds a message:

```java
queue.add(message);
notifyAll();
```

After a consumer removes a message:

```java
String message = queue.poll();
notifyAll();
```

`notifyAll()` wakes all threads waiting on that object's monitor. They then compete to reacquire the monitor and recheck their conditions.

For example:

* A producer adds a message to an empty queue.
* Waiting consumers are notified.
* One consumer acquires the monitor and removes the message.
* Other consumers reacquire the monitor later and check whether the queue is empty again.
* If it is empty, they return to waiting.

Similarly, when a consumer removes a message from a full queue, waiting producers can wake and check whether there is room.

### Why use `notifyAll()` instead of `notify()`?

In this example, producers and consumers wait on the same monitor, but they wait for different conditions.

* Producers wait for available capacity.
* Consumers wait for available messages.

`notify()` wakes one arbitrary waiter. It might wake a thread whose condition is still false, while a thread that could make progress remains asleep.

`notifyAll()` lets all waiting threads recheck their conditions. It is a safer choice for this shared-monitor design, though it can cause extra wakeups.

## 9. Why use `synchronized` methods?

Both queue operations are synchronized:

```java
public synchronized void put(String message)
        throws InterruptedException {
    // Add a message safely
}

public synchronized String take()
        throws InterruptedException {
    // Remove a message safely
}
```

A synchronized instance method acquires the monitor of `this`.

Since both methods belong to the same `MessageQueue` object, only one thread at a time can execute either synchronized method on that object.

However, `wait()` releases that monitor while the thread waits, allowing another thread to enter a synchronized method.

## 10. Understanding `join()`

The main thread uses `join()` to wait for other threads to terminate.

```java
for (Thread producer : producers) {
    producer.join();
}
```

This ensures that all producers finish before the main thread begins sending shutdown messages.

After the shutdown messages are sent, the main thread also waits for all consumers:

```java
for (Thread consumer : consumers) {
    consumer.join();
}
```

Without these joins, `main` could continue before the workers finish their tasks.

## 11. What is a poison pill?

A poison pill is a special message used to tell a consumer to stop processing.

This project defines:

```java
private static final String POISON_PILL =
        "__STOP_CONSUMER__";
```

After all producers finish, the main thread puts three poison pills into the queue—one for each consumer.

A consumer exits when it receives one:

```java
if (POISON_PILL.equals(message)) {
    break;
}
```

The poison pill is not a normal message, so it should not be processed as application data.

### Why send one poison pill per consumer?

Each consumer needs its own stop signal. Sending one poison pill would stop only one consumer, leaving the other consumers potentially waiting forever.

The bounded queue can hold only two messages at a time, so inserting the three poison pills may require waiting for consumers to free space. This is expected.

## 12. Execution flow

1. The main thread creates the shared queue with capacity two.
2. Three consumers start and wait because the queue is initially empty.
3. Three producers start and begin creating messages.
4. Producers add messages while the queue has capacity.
5. Producers wait when the queue is full.
6. Consumers remove messages and notify waiting threads.
7. Producers and consumers continue until all normal messages have been processed.
8. The main thread waits for all producers to finish.
9. The main thread sends one poison pill to each consumer.
10. Consumers receive their poison pills and exit.
11. The main thread waits for all consumers to finish.

## 13. Example output

The output order varies because threads run concurrently. A simplified illustration is:

```text
Producer-1 produced: P1-Message-1 | Queue: [P1-Message-1]
Producer-2 produced: P2-Message-1 | Queue: [P1-Message-1, P2-Message-1]
Consumer-1 consumed: P1-Message-1 | Queue: [P2-Message-1]
Producer-3 produced: P3-Message-1 | Queue: [P2-Message-1, P3-Message-1]
Consumer-2 consumed: P2-Message-1 | Queue: [P3-Message-1]

...

All producers finished. Stopping consumers...

...

All producers and consumers finished.
```

This is an illustrative excerpt, not a guaranteed sequence. The actual order and which consumer receives each message can vary.

The queue's printed contents never exceed its configured capacity during normal operation.

