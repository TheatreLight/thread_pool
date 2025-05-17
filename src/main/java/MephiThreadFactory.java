import java.util.concurrent.ThreadFactory;

public class MephiThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r);
    }
}
