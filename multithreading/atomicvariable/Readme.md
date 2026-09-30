# AtomicInteger & CAS

This folder demonstrates how to use `AtomicInteger` and **Compare-And-Set (CAS)** for thread-safe operations in Java.

## Examples

### 1. CasCounter

Multiple threads increment a shared counter using CAS.

```text
10 threads × 1000 increments = 10000
```

CAS pattern:

```text
GET → CALCULATE → CAS
                  ↓
            Success → Done
            Failure → Retry
```

### 2. LimitedInventory

Multiple threads try to purchase limited stock.

CAS ensures that:

* Stock never becomes negative.
* Each item is purchased only once.
* Multiple threads can safely update the stock.

---

## Why AtomicInteger?

Normal:

```java
counter++;
```

is not atomic because it involves:

```text
READ → MODIFY → WRITE
```

`AtomicInteger` provides atomic operations such as:

```java
incrementAndGet()
decrementAndGet()
compareAndSet()
```

---

## What is CAS?

CAS = **Compare-And-Set**

```java
compareAndSet(expectedValue, newValue)
```

Meaning:

> If the current value is still `expectedValue`, change it to `newValue`.

Returns:

* `true` → update succeeded
* `false` → another thread changed the value

CAS itself performs **one attempt**. A `while` loop is used to retry when CAS fails.

---

## CAS vs synchronized vs volatile

|                | `synchronized`   | `AtomicInteger + CAS` | `volatile` |
| -------------- | ---------------- | --------------------- | ---------- |
| Main purpose   | Mutual exclusion | Atomic operations     | Visibility |
| Uses lock      | Yes              | No explicit lock      | No         |
| Atomic `++`    | Yes              | Yes                   | **No**     |
| Retry required | No               | Often                 | No         |

### Remember

```text
volatile      → Visibility
synchronized  → Lock + Critical Section
AtomicInteger → Atomic operations
CAS           → Compare and update
```

### CAS Mental Model

```text
READ
 ↓
CALCULATE
 ↓
CAS
 ↓
Success → DONE
Failure → RETRY
```
