public class AcessSharedObjects implements Runnable {
    private static final long WARNING_TIMEOUT_MS = 10000;
    private static final long KILL_TIMEOUT_MS = 20000;

    private Object obj1;
    private Object obj2;

    public AcessSharedObjects(Object obj1, Object obj2){
        this.obj1 = obj1;
        this.obj2 = obj2;
    }

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        System.out.println(name + " acquiring lock on " + obj1);
        Thread watchdog1 = startWatchdog(name, obj1);

        synchronized(obj1) {
            watchdog1.interrupt();
            System.out.println(name + " acquired lock on " + obj1);
            work();
            System.out.println(name + " acquiring lock on " + obj2);
            Thread watchdog2 = startWatchdog(name, obj2);

            synchronized(obj2) {
            watchdog2.interrupt();
            System.out.println(name + " acquired lock on " + obj2);
            work();
            }

            System.out.println(name + " released lock on "+ obj2);
        }
        System.out.println(name + " released lock on " + obj1 );
        System.out.println(name + " finished execution.");
    }

    // synchronized cannot time out, so a watchdog tracks how long the thread waits for the lock.
    // It is interrupted as soon as the lock is acquired.
    private Thread startWatchdog(String name, Object obj) {
        Thread watchdog = new Thread(() -> {
            try {
                Thread.sleep(WARNING_TIMEOUT_MS);
                System.out.println("WARNING: " + name + " has been waiting more than "
                    + WARNING_TIMEOUT_MS / 1000 + " seconds for lock on " + obj);
                Thread.sleep(KILL_TIMEOUT_MS - WARNING_TIMEOUT_MS);
                System.out.println("FATAL: " + name + " has been waiting more than "
                    + KILL_TIMEOUT_MS / 1000 + " seconds for lock on " + obj
                    + ". Starvation by deadlock detected, killing execution.");
                System.exit(1);
            }
            catch (InterruptedException ex) {
                // Lock acquired in time
            }
        }, name + "-watchdog");
        watchdog.setDaemon(true);
        watchdog.start();
        return watchdog;
    }

    private void work() {
        try {
        Thread.sleep(1000);
        }
        catch (InterruptedException ex) {
        ex.printStackTrace();
        }
    }
}
