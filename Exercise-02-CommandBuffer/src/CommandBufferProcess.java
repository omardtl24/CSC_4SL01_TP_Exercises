public class CommandBufferProcess {
    private static final long STUCK_TOLERANCE_MILLIS = 5000;

    public static void main(String[] args) throws Exception {
        int totalCommands = 100;
        int sleepTime = 10;
        boolean verbose = true;

        CommandsBuffer synchronizedBuffer = new SynchronizedCommandsBuffer();
        CommandsBuffer nonSynchronizedBuffer = new NonSynchronizedCommandsBuffer();
        CommandsBuffer partialSynchronizedBuffer = new PartialSynchronizedCommandsBuffer();

        experiment(synchronizedBuffer, "SynchronizedCommandsBuffer", totalCommands, sleepTime, verbose);
        experiment(partialSynchronizedBuffer, "PartialSynchronizedCommandsBuffer", totalCommands, sleepTime, verbose);
        experiment(nonSynchronizedBuffer, "NonSynchronizedCommandsBuffer", totalCommands, sleepTime, verbose);
    }

    public static void experiment(CommandsBuffer buffer, String bufferName, int totalCommands, int sleepTime, boolean verbose) throws Exception {
        buffer.setVerbose(verbose);

        String[] baseCommands = {"command1", "command2", "command3"};

        Thread pushThread = new Thread(new PushThread(buffer, baseCommands, totalCommands, sleepTime));
        Thread pullThread = new Thread(new PullThread(buffer, totalCommands, sleepTime));

        pushThread.start();
        pullThread.start();

        awaitOrFail(pushThread, "PushThread", bufferName);
        awaitOrFail(pullThread, "PullThread", bufferName);

        System.out.println("Result using " + bufferName + ":");
        System.out.println("Elements pushed: " + buffer.getPushedCount());
        System.out.println("Elements pulled: " + buffer.getPulledCount());
        System.out.println("Final buffer size: " + buffer.getSize());
        System.out.println("Final buffer state: ");
        System.out.println(buffer.toString());
    }

    private static void awaitOrFail(Thread thread, String threadName, String bufferName) throws InterruptedException {
        thread.join(STUCK_TOLERANCE_MILLIS);
        if (thread.isAlive()) {
            System.err.println("Error: " + threadName + " for " + bufferName
                    + " is stuck (exceeded " + STUCK_TOLERANCE_MILLIS + "ms tolerance).");
        }
    }
}
