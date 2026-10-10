# Java Executor Framework Demo

### 1. ExecutorService and Thread Pools

* `ExecutorService` manages task execution and worker threads.
* `Executors.newFixedThreadPool(3)` creates a pool with up to three worker threads.
* Threads can be reused for multiple tasks.

### 2. Runnable and Callable

* `Runnable` executes a task without returning a result.
* `Callable<V>` executes a task and returns a result.
* `Callable` can also throw checked exceptions.

### 3. Future

* `Future` represents the result of an asynchronous task.
* `get()` waits for the result.
* If a task fails, `get()` throws `ExecutionException`.
* `cancel()` can request cancellation of a task.

### 4. CompletableFuture

* Supports asynchronous task execution and chaining.
* `supplyAsync()` produces a result.
* `thenApply()` transforms the result.
* `thenAccept()` consumes the result.
* `join()` waits for completion and can throw an unchecked `CompletionException` if the computation fails.

### 5. ScheduledExecutorService

* Executes tasks after a delay or periodically.
* `schedule()` runs a task once after a specified delay.
* `scheduleAtFixedRate()` schedules repeated executions based on a fixed rate.
* `scheduleWithFixedDelay()` waits between the end of one execution and the start of the next.

### 6. Graceful Shutdown

* `shutdown()` stops accepting new tasks while allowing submitted tasks to finish.
* `awaitTermination()` waits for termination up to a specified timeout.
* `shutdownNow()` attempts to interrupt running tasks and returns tasks that have not started.

## Key Takeaways

* Thread pools reduce the overhead of creating threads repeatedly.
* `Runnable` is for tasks without results; `Callable` can return results.
* `Future` lets you retrieve results and observe task failures.
* `CompletableFuture` supports asynchronous transformations and composition.
* `ScheduledExecutorService` supports delayed and periodic execution.
* Executors should be shut down properly to release resources.

## Future Improvements

* Use a custom `ThreadPoolExecutor` with a bounded queue.
* Demonstrate task cancellation and timeout handling.
* Combine multiple asynchronous tasks using `CompletableFuture.allOf()`.
* Add periodic health checks using `scheduleWithFixedDelay()`.


