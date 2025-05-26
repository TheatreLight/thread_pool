import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.*;

public class Research {

    public static long fib(int n) {
        if (n <= 1) return n;
        return fib(n - 1) + fib(n - 2);
    }

    static Runnable cpuTask = ()->{
        long res = fib(32);
        //System.out.println(res);
    };
    static Runnable ioTask = ()->{
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    };

    static void benchmark(Executor executor, List<Runnable> tasks) throws InterruptedException {
        long start = System.nanoTime();
        CountDownLatch latch = new CountDownLatch(tasks.size());
        for (Runnable task : tasks) {
            executor.execute(() -> {
                try {
                    task.run();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        long duration = System.nanoTime() - start;
        System.out.printf("Execution time: %.2f ms%n", duration / 1_000_000.0);
    }

    public static void main(String[] args) throws InterruptedException {
        List<Runnable> cpuTasks = new ArrayList<>();
        List<Runnable> ioTasks = new ArrayList<>();

        for (int i = 0; i < 10000; ++i) {
            cpuTasks.add(cpuTask);
            ioTasks.add(ioTask);
        }
        /*
        * Used unlimited LinkedBlockingQueue for custom pool, but with limit for queue size
        * and limited ArrayBlockingQueue for ThreadPoolExecutor with the same queue limit
        * */
        BlockingQueue<Runnable> tasksQueue1 = new LinkedBlockingQueue<>();
        ThreadFactory thrFactory = new MephiThreadFactory();
        MephiThreadPoolExecutor mephiExec = new MephiThreadPoolExecutor(4, 10, 1, 10000,
                TimeUnit.SECONDS, tasksQueue1, thrFactory, new MephiRejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, MephiThreadPoolExecutor executor) {
                throw new RejectedExecutionException();
            }
        });
        BlockingQueue<Runnable> tasksQueue2 = new ArrayBlockingQueue<>(10000);
        ThreadPoolExecutor standardExec = new ThreadPoolExecutor(4, 10, 1,
                TimeUnit.SECONDS, tasksQueue2);

        System.out.println("1st stage: Standard executor CPU benchmark...");
        benchmark(standardExec, cpuTasks);
        System.out.println("Standard executor CPU benchmark is done.");

        System.out.println("2nd stage: Standard executor IO benchmark...");
        benchmark(standardExec, ioTasks);
        System.out.println("Standard executor IO benchmark is done.");

        standardExec.shutdown();

        System.out.println("3rd stage: Custom executor CPU benchmark...");
        benchmark(mephiExec, cpuTasks);
        System.out.println("Custom executor CPU benchmark is done.");

        System.out.println("4th stage: Custom executor IO benchmark...");
        benchmark(mephiExec, ioTasks);
        System.out.println("Custom executor IO benchmark is done.");

        mephiExec.shutdown();

    }
}
