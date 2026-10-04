
// A thread acquires the same lock in processOrder(), then calls updateInventory(), which acquires the same lock again.
// Normally, trying to acquire a lock that you already hold could lead to a deadlock. However, ReentrantLock is designed to be reentrant, which means the same thread can acquire the same lock multiple times.
// Each successful lock() must have a corresponding unlock().

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






