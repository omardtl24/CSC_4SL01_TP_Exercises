public class PullThread implements Runnable {
    private CommandsBuffer buffer;
    private int sleepTime;
    private int totalCommands;

    public PullThread(CommandsBuffer buffer,
                      int totalCommands,
                      int sleepTime) {
        this.buffer = buffer;
        this.sleepTime = sleepTime;
        this.totalCommands = totalCommands;
    }

    @Override
    public void run() {
        for (int i = 0; i < totalCommands; i++) {
            buffer.popCommand();
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
