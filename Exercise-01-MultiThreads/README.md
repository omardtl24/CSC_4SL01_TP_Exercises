## Exercise 01 — Basic Multi-Threads

### Targets

- Define a `Thread` subclass called `MyThread`, where each instance receives a name when it is created.
- Its `run()` method prints a message 100 times, showing the thread's name and the iteration index (1 to 100), sleeps 50 ms between iterations, and prints an END message before exiting.
- In a main method, create 2 instances of `MyThread` and start them.
- Answer: what is the order of the printed messages? Is it the same on every run? Same questions when adding more threads or changing the sleep delays.

### Solution

- `MyThread` extends `Thread`. Each instance receives a name, a number of iterations (`target`) and a sleep time. Its `run()` method prints the thread's name and the iteration index for each iteration, then sleeps once for the given time before ending.
- `MultiThreads` holds the experiment. The `experimentwithThreads` method creates the requested number of `MyThread` instances (named "Thread 1", "Thread 2", ...), starts all of them, and then calls `join` on each one, so the main program waits until every thread has finished.
- The number of threads, iterations and the sleep time are parameters, which makes it easy to try the variations asked in the questions. The current run uses 3 threads, 5 iterations and a 50 ms sleep.

### Differences with the targets

- The thread sleeps only once, after all its iterations, instead of 50 ms between each iteration.
- The iteration index goes from 0 to `target - 1` instead of 1 to 100, and the default run uses 5 iterations instead of 100.
- No END message is printed when the thread finishes.
- The default run starts 3 threads instead of 2.

### Questions

1. What is the order of the message printouts?

With the current settings, each thread prints all of its messages in one block: for example all of "Thread 3", then all of "Thread 2", then all of "Thread 1". The order of the threads does not follow the order in which they were started. Five short prints take so little time that a thread usually finishes them before the scheduler switches to another thread.

2. Execute several times: is the order the same each time?

No. The messages of one thread always appear in their own order (0, 1, 2, ...), but the order of the threads changes from one run to the next (for example 3-2-1, then 2-1-3, then 2-3-1). The operating system scheduler decides when each thread runs, so the order is not deterministic.

3. Same questions when adding more threads or changing the sleep delays.

Adding more threads gives more possible orders, so the result is even less predictable. With the current code, the sleep happens after all the prints, so changing the delay only changes how long the program takes to end, not the order of the messages. If the sleep is placed between iterations, as the target asks, each thread gives up the processor after every message, and the messages of the different threads become mixed together, with a different mix on each run.
