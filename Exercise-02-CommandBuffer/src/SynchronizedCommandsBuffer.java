public class SynchronizedCommandsBuffer extends CommandsBuffer{

    public SynchronizedCommandsBuffer() {
        super();
    }

    @Override
    public synchronized String popCommand() {
        try {
            while (true) {
                if (nextTakeIndex == nextStoreIndex) {
                    wait();
                }
                else {
                    String cmd = commands[nextTakeIndex];
                    nextTakeIndex = (nextTakeIndex + 1) % 1024;
                    pulledCount++;
                    log("POP cmd=" + cmd + " -> " + getStatus());
                    notifyAll();
                    return cmd;
                }
            }
        }
        catch (Exception ex) {
        }
        return null;
    }

    @Override
    public synchronized void pushCommand(String cmd) {
        try {
            while (true) {
                int futureStoreIndex = (nextStoreIndex + 1) % 1024;
                if (nextTakeIndex == futureStoreIndex) {
                    wait();
                }
                else {
                    commands[futureStoreIndex] = cmd;
                    nextStoreIndex = futureStoreIndex;
                    pushedCount++;
                    log("PUSH cmd=" + cmd + " -> " + getStatus());
                    notifyAll();
                    return;
                }
            }
        }
        catch (Exception ex) {
        }
    }

}