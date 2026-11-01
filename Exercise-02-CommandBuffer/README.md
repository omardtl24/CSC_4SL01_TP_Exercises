## Exercise 02 — Command Buffer

### Questions

1. As the two threads push and pull 100 commands each, the buffer should be empty at the end — is this so?

Yes, as the commands buffer uses synchronized operations, it allows the threads to wait and act in the corresponding moments to ensure that the push and pop procedures are done on its correct order, even though they're separated processes

2. Remove all synchronisation commands from the push and pull methods (`synchronized`, `wait` and `notifyAll`) — is the outcome any different? Why?

When removing synchronisation commands, it can not be guaranteed that the outcome follows the expected behaviour. In some runs, it finishes the process correctly, but in others the two threads race on the same `nextStoreIndex`/`nextTakeIndex` fields without any happens-before relationship between them, so a thread can fail to ever see an update made by the other (or see a partially updated value). This most often shows up as PushThread or PullThread getting stuck forever spinning in their `while(true)` busy-loop, since there is no `wait`/`notifyAll` to suspend and wake them up, and it can also leave `pushedCount`/`pulledCount` or the buffer indices in an inconsistent state.

3. Leave synchronisation and remove the `wait` and `notify` commands from the push method — what happens?

Since `pushCommand` is still `synchronized`, push and pop remain mutually exclusive, so the buffer indices stay consistent (no corruption like in question 2). The problem is the missing `notifyAll()`: when PullThread finds the buffer empty and calls `wait()` inside `popCommand`, nothing ever wakes it up again, because `pushCommand` no longer signals the monitor after storing a command. In practice PullThread hits this empty buffer right at the start of the run, before PushThread has pushed anything, so it blocks on `wait()` and stays stuck there for the rest of the execution — a classic lost wakeup. PushThread, on the other hand, keeps pushing normally, since with only 100 commands and a 1023-slot capacity it never needs to wait. The result: PushThread finishes, PullThread never does, `awaitOrFail` reports it as stuck after the 5 second tolerance, `pulledCount` stays frozen near 0, and the JVM never actually exits afterwards because a live, non-daemon thread is left waiting forever.
