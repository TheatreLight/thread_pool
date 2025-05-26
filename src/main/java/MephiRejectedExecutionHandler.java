public interface MephiRejectedExecutionHandler {
    void rejectedExecution(Runnable r, MephiThreadPoolExecutor executor);
}
