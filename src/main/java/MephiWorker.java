import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class MephiWorker implements Runnable {
    private BlockingQueue<Runnable> taskQueue;
    private final String name;
    private boolean running = true;
    private final MephiLogger log = new MephiLogger();
    private final Consumer<MephiWorker> killer;
    long keepAliveTime;
    TimeUnit timeUnit;

    private void kill() {
        running = false;
        killer.accept(this);
    }

    public MephiWorker(BlockingQueue<Runnable> queue, long keepAlive, TimeUnit tUnit, Consumer<MephiWorker> killer) {
        log.MEPHI_LOG_INFO("MephiWorker","","MephiWorker","Creating the new worker...");
        this.taskQueue = queue;
        this.name = "Worker_#" + Thread.currentThread().getName();
        this.keepAliveTime = keepAlive;
        this.timeUnit = tUnit;
        this.killer = killer;
    }

    @Override
    public void run() {
        log.MEPHI_LOG_INFO("MephiWorker", Thread.currentThread().getName(),
                "run", "Worker_#" + Thread.currentThread().getName() + " is ran.");
        while (running || !taskQueue.isEmpty()) {
            try {
                var t = taskQueue.poll(keepAliveTime, timeUnit);
                if (t != null) {
                    t.run();
                } else {
                    kill();
                }
            } catch (InterruptedException e) {
                kill();
                throw new RuntimeException(e);
            }
        }
    }

    public String getName() {
        return name;
    }

    public Thread getThread() {
        return Thread.currentThread();
    }

    public void shutdown() {
        log.MEPHI_LOG_INFO("MephiWorker", Thread.currentThread().getName(),
                "shutdown", "Stopping the worker '" + name + "'");
        running = false;
    }
}
