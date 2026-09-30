import java.util.concurrent.atomic.AtomicInteger;

public class LimitedInventory {

    private final AtomicInteger stock;

    public LimitedInventory(int initialStock) {
        this.stock = new AtomicInteger(initialStock);
    }

    /**
     * Attempts to purchase one item.
     *
     * Returns true if the purchase was successful.
     * Returns false if the inventory is empty.
     *
     * Uses CAS to safely perform:
     *
     *     check stock
     *     decrease stock
     *
     * as one atomic state transition.
     */
    public boolean purchase() {

        while (true) {

            // Read the current stock
            int currentStock = stock.get();

            // Nothing available
            if (currentStock <= 0) {
                return false;
            }

            // Try to decrease stock by 1
            if (stock.compareAndSet(
                    currentStock,
                    currentStock - 1)) {

                // Purchase successful
                return true;
            }

            /*
             * CAS failed.
             *
             * Another thread changed the stock
             * after we read it.
             *
             * Retry by reading the latest stock.
             */
        }
    }

    public int getStock() {
        return stock.get();
    }

    public static void main(String[] args) throws InterruptedException {

        LimitedInventory inventory = new LimitedInventory(5);

        Thread[] customers = new Thread[10];

        // Create 10 customers
        for (int i = 0; i < customers.length; i++) {

            int customerId = i + 1;

            customers[i] = new Thread(() -> {

                if (inventory.purchase()) {

                    System.out.println(
                            "Customer " + customerId +
                            " purchased successfully"
                    );

                } else {

                    System.out.println(
                            "Customer " + customerId +
                            " failed - Out of stock"
                    );
                }
            });
        }

        // Start all customers
        for (Thread customer : customers) {
            customer.start();
        }

        // Wait for all customers to finish
        for (Thread customer : customers) {
            customer.join();
        }

        System.out.println(
                "Remaining stock: " + inventory.getStock()
        );
    }
}
