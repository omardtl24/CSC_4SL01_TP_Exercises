## Exercise 02 — Command Buffer

### Targets

- Define a `CommandsBuffer` class that stores commands in a `String[]` array and provides push and pop methods to add and remove commands.
- Define two thread types, `PushThread` and `PullThread`, that both receive a `CommandsBuffer` when they are created and act on it for 100 iterations, with a 50 ms sleep between iterations.
- The main method creates one thread of each type, starts them, and uses `join` at the end.
- Answer the three questions below.

### Solution

The solution builds a circular buffer of commands shared by a producer thread and a consumer thread, and compares three versions of it that use different amounts of synchronisation.

- `CommandsBuffer` is the abstract base class. It stores up to 1024 commands in an array used as a circular queue, with one index for the next position to write and one for the next position to read. It also counts how many commands were pushed and pulled, can print a log line for each operation (verbose mode), and gives the current size and content of the buffer.
- `SynchronizedCommandsBuffer` is the correct version. Both `pushCommand` and `popCommand` are `synchronized`. When the buffer is full, the producer calls `wait()`; when it is empty, the consumer calls `wait()`. After each push or pop, the method calls `notifyAll()` to wake up the other thread.
- `NonSynchronizedCommandsBuffer` removes all synchronisation. Both methods keep looping (busy-waiting) until there is space or a command available, and nothing protects the shared indices.
- `PartialSynchronizedCommandsBuffer` keeps both methods `synchronized`, but removes `wait()` and `notifyAll()` from `pushCommand` only. `popCommand` stays the same as in the correct version.
- `PushThread` pushes a given number of commands (cycling through "command1", "command2" and "command3"), and `PullThread` pulls the same number. Both sleep a short time between operations.
- `CommandBufferProcess` runs the experiment on each version: it starts one push thread and one pull thread with 100 commands each, waits for them with a 5 second limit, and reports any thread that is still running after that time as stuck. It then prints how many commands were pushed and pulled and the final content of the buffer.
- The sleep between iterations is currently 10 ms instead of the 50 ms asked in the statement. `join` is called with the 5 second limit so that a stuck thread is reported instead of blocking the whole experiment.

### Questions

1. As the two threads push and pull 100 commands each, the buffer should be empty at the end — is this so?

Yes, as the commands buffer uses synchronized operations, it allows the threads to wait and act in the corresponding moments to ensure that the push and pop procedures are done on its correct order, even though they're separated processes

2. Remove all synchronisation commands from the push and pull methods (`synchronized`, `wait` and `notifyAll`) — is the outcome any different? Why?

When removing synchronisation commands, it can not be guaranteed that the outcome follows the expected behaviour. In some runs, it finishes the process correctly, but in others the two threads race on the same `nextStoreIndex`/`nextTakeIndex` fields without any happens-before relationship between them, so a thread can fail to ever see an update made by the other (or see a partially updated value). This most often shows up as PushThread or PullThread getting stuck forever spinning in their `while(true)` busy-loop, since there is no `wait`/`notifyAll` to suspend and wake them up, and it can also leave `pushedCount`/`pulledCount` or the buffer indices in an inconsistent state.

3. Leave synchronisation and remove the `wait` and `notify` commands from the push method — what happens?

Since `pushCommand` is still `synchronized`, push and pop remain mutually exclusive, so the buffer indices stay consistent (no corruption like in question 2). The problem is the missing `notifyAll()`: when PullThread finds the buffer empty and calls `wait()` inside `popCommand`, nothing ever wakes it up again, because `pushCommand` no longer signals the monitor after storing a command. In practice PullThread hits this empty buffer right at the start of the run, before PushThread has pushed anything, so it blocks on `wait()` and stays stuck there for the rest of the execution — a classic lost wakeup. PushThread, on the other hand, keeps pushing normally, since with only 100 commands and a 1023-slot capacity it never needs to wait. The result: PushThread finishes, PullThread never does, `awaitOrFail` reports it as stuck after the 5 second tolerance, `pulledCount` stays frozen near 0, and the JVM never actually exits afterwards because a live, non-daemon thread is left waiting forever.
