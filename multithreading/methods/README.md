# Java Thread Methods



## 1. `Thread.currentThread()`

### What does it do?

`Thread.currentThread()` returns a reference to the thread that is currently executing the code.

It does not return a thread that you created by name or stored in a variable. It returns the thread executing that particular line.


---

## 2. `getName()` and `setName()`

### What do they do?

* `getName()` returns the thread's name.
* `setName()` changes the thread's name.

Giving threads meaningful names makes logs and debugging easier.


### When are these useful?

Names such as `Payment-Worker`, `Order-Worker`, and `Email-Worker` make it easier to identify which thread is performing a task.

---

## 3. `isAlive()`

### What does it do?

`isAlive()` returns `true` if a thread has started and has not yet terminated.

It returns `false` before the thread starts and after it finishes.


### Why?

1. Before `start()`, the thread is in the `NEW` state, so `isAlive()` returns `false`.
2. The thread starts and executes its task.
3. `join()` waits until the worker terminates.
4. After `join()` returns, `isAlive()` returns `false`.

### Important points

* `isAlive()` checks the thread's status at that moment.
* Immediately after `start()`, it is not guaranteed to return `true`, because the thread could finish very quickly.
* `isAlive()` does not wait for a thread to finish. Use `join()` when you need to wait.

---

## 4. `getState()`

### What does it do?

`getState()` returns the thread's current state as a value of type `Thread.State`.

Java defines six thread states.

| State           | Meaning                                          |
| --------------- | ------------------------------------------------ |
| `NEW`           | Created but not started                          |
| `RUNNABLE`      | Ready to run or currently running                |
| `BLOCKED`       | Waiting to acquire a `synchronized` monitor lock |
| `WAITING`       | Waiting indefinitely for another action          |
| `TIMED_WAITING` | Waiting for a limited time                       |
| `TERMINATED`    | Execution has finished                           |

### Examples of waiting states

* `Thread.sleep(1000)` generally puts the executing thread in `TIMED_WAITING`.
* `object.wait()` without a timeout generally puts the thread in `WAITING`.
* `thread.join()` without a timeout generally puts the calling thread in `WAITING`.
* Waiting to enter a `synchronized` block can put a thread in `BLOCKED`.

### Important points

* Java does not expose a separate `RUNNING` state. Running and ready-to-run threads are both represented by `RUNNABLE`.
* A thread's state can change quickly.
* `getState()` is useful for monitoring and debugging, not for reliable thread coordination.

---

## 5. `Thread.yield()`

### What does it do?

`Thread.yield()` is a static method that hints to the scheduler that the current thread is willing to let other runnable threads have an opportunity to execute.


### Important points

* `yield()` is only a hint.
* It does not guarantee that another thread will run next.
* It does not guarantee a context switch.
* It does not release locks held by the current thread.
* It does not make the current thread wait until another thread finishes.

In normal application development, do not use `yield()` to coordinate threads.

---

## 6. `Thread.sleep()`

### What does it do?

`Thread.sleep()` pauses the currently executing thread for approximately the specified duration, subject to scheduling and system timing.


### Important points

* `sleep()` is static and pauses the current thread.
* It does not release any monitor locks the thread holds.
* It can throw `InterruptedException`.
* The actual delay can be longer than the requested duration.

---

## 7. `Thread.join()`

### What does it do?

`join()` makes the calling thread wait until the specified thread terminates.

It is useful when one thread must finish before another continues.

The main thread waits until the worker has terminated.

### Important points

* `join()` is called on the thread you want to wait for.
* It makes the calling thread wait, not the target thread.
* It can throw `InterruptedException`.
* `join()` does not release locks held by the calling thread.

### Difference between `sleep()` and `join()`

| `sleep()`                                | `join()`                              |
| ---------------------------------------- | ------------------------------------- |
| Pauses the current thread for a duration | Waits for another thread to terminate |
| Takes a time duration                    | Called on a target thread             |
| Does not release held monitor locks      | Does not release held monitor locks   |

---

## 8. `interrupt()`

### What does it do?

`interrupt()` requests interruption of a thread. It does not forcibly terminate the thread.

The target thread must respond to the request appropriately.

### Important points

* Interrupting a thread during `sleep()`, `wait()`, or `join()` can cause `InterruptedException`.
* When `InterruptedException` is thrown, the interrupt status is cleared.
* If the code cannot fully handle the interruption, restoring the status with `Thread.currentThread().interrupt()` is a common practice.
* A thread executing a CPU loop must check its interrupt status or otherwise respond to interruption.

---

## 9. `isInterrupted()` and `Thread.interrupted()`

These methods check a thread's interrupt status, but they behave differently.

| Method                   | Checks which thread? | Clears the flag? |
| ------------------------ | -------------------- | ---------------- |
| `thread.isInterrupted()` | The specified thread | No               |
| `Thread.interrupted()`   | The current thread   | Yes, if set      |

### Why?

1. `interrupt()` sets the current thread's interrupt status.
2. `isInterrupted()` returns `true` without clearing it.
3. `Thread.interrupted()` returns `true` and clears it.
4. The final `isInterrupted()` returns `false`.

### Important points

* `isInterrupted()` is an instance method.
* `Thread.interrupted()` is a static method.
* `Thread.interrupted()` checks and clears the current thread's status, not an arbitrary target thread's status.

---

## 10. `setDaemon()` and `isDaemon()`

### What is a daemon thread?

A daemon thread performs background work and does not, by itself, keep the JVM alive.

### Important points

* `setDaemon(true)` marks the thread as a daemon.
* `isDaemon()` checks whether it is a daemon.
* The daemon setting must be configured before `start()`.
* The JVM may exit when no live non-daemon threads remain, even if daemon threads are still running.
* Daemon threads are not appropriate for essential tasks that must finish, such as saving critical data.

---

