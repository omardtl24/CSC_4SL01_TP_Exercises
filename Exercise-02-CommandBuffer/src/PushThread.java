public class PushThread implements Runnable {
    private CommandsBuffer buffer;
    private String[] commands;
    private int totalCommands;
    private int sleepTime;

    public PushThread(CommandsBuffer buffer,
                      String[] baseCommands,
                      int totalCommands,
                      int sleepTime) {
        this.buffer = buffer;
        this.sleepTime = sleepTime;
        this.totalCommands = totalCommands;
        this.commands = baseCommands;
    }

    @Override
    public void run() {
        for (int i = 0; i < totalCommands; i++) {
            buffer.pushCommand(commands[i % commands.length]);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
