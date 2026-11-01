public class PartialSynchronizedCommandsBuffer extends CommandsBuffer {

    public PartialSynchronizedCommandsBuffer() {
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
        while (true) {
            int futureStoreIndex = (nextStoreIndex + 1) % 1024;
            if (nextTakeIndex == futureStoreIndex) {
                continue;
            }
            else {
                commands[futureStoreIndex] = cmd;
                nextStoreIndex = futureStoreIndex;
                pushedCount++;
                log("PUSH cmd=" + cmd + " -> " + getStatus());
                return;
            }
        }
    }

}
