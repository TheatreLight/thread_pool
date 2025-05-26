public class MephiLogger {
    private final String thread;
    private final String infoLevel = "[INFO]";
    private final String errorLevel = "[ERROR]";
    private static final boolean IS_LOGGING_ENABLED = false;

    public MephiLogger() {
        this.thread = "[" + Thread.currentThread().getName() + "]";
    }
    public void MEPHI_LOG_INFO(String className, String thread, String method, String info) {
        if (!IS_LOGGING_ENABLED) return;
        String threadName = thread.isEmpty() ? this.thread : "["+thread+"]";
        System.out.println(infoLevel + threadName + " " + className + ": " + method + ": " + info);
    }

    public void MEPHI_LOG_ERROR(String className, String method, String info) {
        if (!IS_LOGGING_ENABLED) return;
        System.out.println(errorLevel + this.thread + " " + className + ": " + method + ": " + info);
    }
}
