import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class ThreadExchangeManager<T> {
    private final LinkedList<T> socketsList = new LinkedList<>();
    private final int MAX_QUEUE_SIZE = 10;

    public void addClientSocket(T clientSocket) throws InterruptedException {
        synchronized (socketsList) {
            if (socketsList.size() < MAX_QUEUE_SIZE) {
                socketsList.add(clientSocket);
                socketsList.notifyAll();
            } else {
                socketsList.wait();
            }
        }
    }

    public Optional<T> retriveSocket() {
        synchronized (socketsList) {
            try {
                return Optional.of(socketsList.remove());
            } catch (NoSuchElementException e) {
                return Optional.empty();
            }
        }
    }

    public T blockingRetrieveSocket() throws InterruptedException {
        synchronized (socketsList) {
            while (socketsList.isEmpty()) {
                socketsList.wait();
            }
            T removedSocket = socketsList.remove();
            socketsList.notifyAll();
            return removedSocket;
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        ThreadExchangeManager<Integer> integerThreadExchangeManager = new ThreadExchangeManager<>();

        Thread thread = new Thread(() -> IntStream.range(0, 1000).forEach(value -> {
            try {
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(10, 20));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            try {
                integerThreadExchangeManager.addClientSocket(value);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        Thread thread2 = new Thread(() -> IntStream.range(1000, 2000).forEach(value -> {
            try {
                TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(10, 20));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            try {
                integerThreadExchangeManager.addClientSocket(value);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }));

        thread.start();
        thread2.start();

        IntStream.range(0, 10).forEach(threadCount -> {
            Thread thread1 = new Thread(new Counter<Integer>(integerThreadExchangeManager, 2000));
            thread1.setName("Reader: " + threadCount);
            thread1.start();
        });

        thread.join();
        thread2.join();
//        TimeUnit.MILLISECONDS.sleep(40_000);
        System.in.read();
    }

    static class Counter<T> implements Runnable {

        private final ThreadExchangeManager<T> threadExchangeManager;
        private final int maxCount;

        Counter(ThreadExchangeManager<T> threadExchangeManager, int maxCount) {
            this.threadExchangeManager = threadExchangeManager;
            this.maxCount = maxCount;
        }

        @Override
        public void run() {
            int itemCount = 0;
            for (int i = 0; i < maxCount; i++) {
                if (threadExchangeManager.retriveSocket().isPresent()) {
//                    T integer = threadExchangeManager.retriveSocket();
                    ++itemCount;
                }
            }
            LogUtil.log("Thread Count: " + itemCount);
        }
    }
}
