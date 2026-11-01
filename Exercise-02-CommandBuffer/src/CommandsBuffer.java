public abstract class CommandsBuffer {
    // An array to store the commands to be executed
    protected String[] commands = new String[1024];
    // Index where to store the next arriving command
    // Condition: (nextStoreIndex + 1) % 1024 != lastTakeIndex
    protected int nextStoreIndex = 0;
    
    protected int nextTakeIndex = 0;

    // Verbose status tracking
    protected long pushedCount = 0;
    protected long pulledCount = 0;
    protected boolean verbose = false;

    public abstract void pushCommand(String cmd);

    public abstract String popCommand();

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public long getPushedCount() {
        return pushedCount;
    }

    public long getPulledCount() {
        return pulledCount;
    }

    public String getStatus() {
        return "pushed=" + pushedCount + ", pulled=" + pulledCount + ", currentSize=" + getSize();
    }

    protected void log(String message) {
        if (verbose) {
            System.out.println("[" + getClass().getSimpleName() + "] " + message);
        }
    }

    public int getSize() {
        if (nextStoreIndex >= nextTakeIndex) {
            return nextStoreIndex - nextTakeIndex;
        }
        else {
            return 1024 - (nextTakeIndex - nextStoreIndex);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CommandsBuffer: [");
        int size = getSize();
        for (int i = 0; i < size; i++) {
            sb.append(commands[(nextTakeIndex + i) % commands.length]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
