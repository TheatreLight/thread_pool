import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MephiThreadPoolExecutor implements CustomExecutor {
    private final int corePoolSize;
    private final int maxPoolSize;
    private final long keepAliveTime;
    private final TimeUnit timeUnit;
    private int queueSize = -1;
    private int minSpareThreads;
    private final BlockingQueue<Runnable> taskQueue;
    private final List<MephiWorker> workers = new ArrayList<>();
    private final List<Thread> threads = new ArrayList<>();
    private final ThreadFactory threadFactory;
    private MephiRejectedExecutionHandler handler;
    private final AtomicInteger count = new AtomicInteger(0);
    private final MephiLogger log = new MephiLogger();
    private boolean isShuttingDown = false;

    private void killWorker(MephiWorker mw) {
        int index = threads.indexOf(mw.getThread());
        if (index >= corePoolSize) {
            workers.remove(index);
            threads.remove(index);
        }
    }

    public MephiThreadPoolExecutor(int corePoolSize,
                                   int maximumPoolSize,
                                   long keepAliveTime,
                                   int queueSize,
                                   TimeUnit unit,
                                   BlockingQueue<Runnable> workQueue,
                                   ThreadFactory threadFactory,
                                   MephiRejectedExecutionHandler handler) {
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
        this.queueSize = queueSize;
        this.taskQueue = workQueue;
        this.threadFactory = threadFactory;
        this.handler = handler;
    }

    private void addWorker() {
        MephiWorker mw = new MephiWorker(taskQueue, keepAliveTime, timeUnit, this::killWorker);
        workers.add(mw);
        var t = threadFactory.newThread(mw);
        t.start();
        threads.add(t);
        log.MEPHI_LOG_INFO("MephiThreadPoolExecutor", "","addWorker",
                "Worker 'Worker_#" + t.getName() + "' was added");
    }

    @Override
    public void execute(Runnable command) {
        log.MEPHI_LOG_INFO("MephiThreadPoolExecutor", "","execute", "enter");
        if (command == null) {
            log.MEPHI_LOG_ERROR("MephiThreadPoolExecutor", "execute","Command is null!");
            throw new NullPointerException();
        }
        log.MEPHI_LOG_INFO("MephiThreadPoolExecutor", "","execute", "the task added in queue");
        boolean isQueueOverflow = queueSize >= 0 && taskQueue.size() == queueSize; // if queueSize <= 0 we decide it is unlimited queue
        if (isQueueOverflow || !taskQueue.offer(command)) {
            if (workers.size() < maxPoolSize) {
                log.MEPHI_LOG_INFO("MephiThreadPoolExecutor", "","execute",
                        "The base number of workers is not enough, create additional workers.");
                addWorker();
            } else {
                log.MEPHI_LOG_ERROR("MephiThreadPoolExecutor", "execute", "Can't add task to queue.");
                handler.rejectedExecution(command, this);
            }
        }

        if (workers.size() < corePoolSize) {
            log.MEPHI_LOG_INFO("MephiThreadPoolExecutor", "","execute", "Add the base number of workers.");
            addWorker();
        }
    }

    @Override
    public <T> Future<T> submit(Callable<T> callable) {
        return null;
    }

    @Override
    public void shutdown() {
        isShuttingDown = true;
        for (var w : workers) {
            w.shutdown();
        }
    }

    @Override
    public void shutdownNow() {
        shutdown();
        for (var t : threads) {
            log.MEPHI_LOG_INFO("MephiThreadPoolExecutor", "",
                    "shutdownNow", "the thread '" + t.getName() + "' is interrupted");
            t.interrupt();
        }
    }
}
