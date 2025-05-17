import java.util.Queue;
import java.util.concurrent.*;

public class Main {
    BlockingQueue<Runnable> tasksQueue = new LinkedBlockingQueue<>();
    ThreadFactory factory = new MephiThreadFactory();
    RejectedExecutionHandler handler = null;
    MephiThreadPoolExecutor exec = new MephiThreadPoolExecutor(4, 4, 10,
            TimeUnit.SECONDS, tasksQueue, factory, handler);
}
