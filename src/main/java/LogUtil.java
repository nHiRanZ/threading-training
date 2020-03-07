import java.util.Date;

public class LogUtil {
    public static void log(String message) {
        System.out.format("[%s] [%s] [%s] [%s]\n", Thread.currentThread().getName(),
                Thread.currentThread().isDaemon(), new Date(), message);
    }
}
