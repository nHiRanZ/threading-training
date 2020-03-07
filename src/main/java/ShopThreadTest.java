import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class ShopThreadTest {

    public static void main(String[] args) {
        int orderCount = 100_000;

        IntStream.range(0, orderCount)
                .forEach(value -> {
                    Thread thread = new Thread(() -> {
                        ShopThreadTest shopThreadTest = new ShopThreadTest();
                        try {
                            shopThreadTest.createOrderByCustomer(value);
                            shopThreadTest.fetchItems(value);
                            shopThreadTest.calculateBill(value);
                            shopThreadTest.charge(value);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                    thread.setDaemon(false);
                    thread.setName("Shop Thread: " + value);
                    thread.start();
                });

        LogUtil.log("Main End");
    }

    private void createOrderByCustomer(int orderId) throws InterruptedException {
        LogUtil.log("Create Order: " + orderId);
        TimeUnit.SECONDS.sleep(2);
    }

    private void fetchItems(int orderId) throws InterruptedException {
        LogUtil.log("Fetch Items: " + orderId);
        TimeUnit.SECONDS.sleep(5);
    }

    private void calculateBill(int orderId) throws InterruptedException {
        LogUtil.log("Calculate Bill: " + orderId);
        TimeUnit.SECONDS.sleep(1);
    }

    private void charge(int orderId) throws InterruptedException {
        LogUtil.log("Charge: " + orderId);
        TimeUnit.SECONDS.sleep(3);
    }
}
