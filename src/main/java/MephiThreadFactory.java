import java.util.concurrent.ThreadFactory;

public class MephiThreadFactory implements ThreadFactory {
    private final MephiLogger log = new MephiLogger();

    @Override
    public Thread newThread(Runnable r) {
        log.MEPHI_LOG_INFO("MephiThreadFactory","","newThread","Creating new thread...");
        return new Thread(r);
    }
}
