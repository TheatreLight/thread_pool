import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) {
        MephiLogger log = new MephiLogger();
        BlockingQueue<Runnable> tasksQueue = new LinkedBlockingQueue<>();
        ThreadFactory factory = new MephiThreadFactory();
        MephiRejectedExecutionHandler handler = new MephiRejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, MephiThreadPoolExecutor executor) {
                throw new RejectedExecutionException();
            }
        };

        MephiThreadPoolExecutor pool = new MephiThreadPoolExecutor(4, 4, 10, 10,
                TimeUnit.SECONDS, tasksQueue, factory, handler);

        for (int i = 0; i < 10; ++i) {
            log.MEPHI_LOG_INFO("Main", "","Main", "---------iteration " + i + "------------------");
            pool.execute(() -> {
                var thName = Thread.currentThread().getName();
                log.MEPHI_LOG_INFO("Runnable", thName, "Task","EXECUTE >>>>>>>>>>>");
                System.out.println("Doing some work, please wait...");
                try {
                    for (int j = 0; j < 5; ++j) {
                        Thread.sleep(1000);
                        System.out.println("[" + thName + "]" + j + " task stage, wait...");
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        pool.shutdown();
    }
}
