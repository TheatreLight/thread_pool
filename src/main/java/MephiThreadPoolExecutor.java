import java.util.concurrent.*;

public class MephiThreadPoolExecutor implements CustomExecutor {
    private int corePoolSize;
    private int maxPoolSize;
    private long keepAliveTime;
    private TimeUnit timeUnit;
    private int queueSize;
    private int minSpareThreads;
    BlockingQueue<Runnable> workQueue;
    private volatile ThreadFactory threadFactory;
    RejectedExecutionHandler handler;

    public MephiThreadPoolExecutor(int corePoolSize,
                                   int maximumPoolSize,
                                   long keepAliveTime,
                                   TimeUnit unit,
                                   BlockingQueue<Runnable> workQueue,
                                   ThreadFactory threadFactory,
                                   RejectedExecutionHandler handler) {
        if (corePoolSize < 0 ||
                maximumPoolSize <= 0 ||
                maximumPoolSize < corePoolSize ||
                keepAliveTime < 0)
            throw new IllegalArgumentException();
        if (workQueue == null || threadFactory == null || handler == null)
            throw new NullPointerException();
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.timeUnit = unit;
        this.workQueue = workQueue;
        this.threadFactory = threadFactory;
        this.handler = handler;
    }

    @Override
    public void execute(Runnable command) {

    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        return null;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void shutdownNow() {

    }

    public ThreadFactory getThreadFactory() {
        return threadFactory;
    }
}
