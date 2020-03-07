import java.net.Socket;
import java.util.stream.IntStream;

public class ClientConnProcessor implements Runnable {
    private final ThreadExchangeManager<Socket> threadExchangeManager;

    public ClientConnProcessor(ThreadExchangeManager<Socket> threadExchangeManager) {
        this.threadExchangeManager = threadExchangeManager;
    }

    @Override
    public void run() {
        IntStream.range(0, 10).forEach(value -> {
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        Socket socket = threadExchangeManager.blockingRetrieveSocket();
                        new EchoSessionApi(socket).run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.setName("thread-processor-"+ value);
            thread.start();
        });
    }
}
