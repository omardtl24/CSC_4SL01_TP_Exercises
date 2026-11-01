public class NonSynchronizedCommandsBuffer extends CommandsBuffer {

    public NonSynchronizedCommandsBuffer() {
        super();
    }

    @Override
    public String popCommand() {
        while (true) {
            if (nextTakeIndex == nextStoreIndex) {
                continue;
            }
            else {
                String cmd = commands[nextTakeIndex];
                nextTakeIndex = (nextTakeIndex + 1) % 1024;
                pulledCount++;
                log("POP cmd=" + cmd + " -> " + getStatus());
                return cmd;
            }
        }
    }

    @Override
    public void pushCommand(String cmd) {
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
