## Exercise 03 — Deadlock

### Targets

- Use the given `AcessSharedObjects` class: a task that locks a first shared object, works for one second, then locks a second object while still holding the first one.
- Run the given `DeadlockExample` program, where three threads share three objects, and explain what happens.
- Propose a solution to the problem found.

### Solution

- `AcessSharedObjects` is the task each thread runs, as given in the statement. It prints a message each time it tries to take, takes or releases a lock.
- `DeadlockExample` is the given program: it starts three threads, half a second apart, on three shared objects.
- `NonDeadlockExample` is the proposed solution (see below). It also waits for the three threads with `join` and prints a message when all of them have finished.
- A watchdog added to `AcessSharedObjects` detects a thread stuck waiting for a lock (see "Deadlock detection").

### Questions

1. Run this program. What happens?

The program never ends: it is a deadlock. Thread 1 locks obj1 then asks for obj2, thread 2 locks obj2 then asks for obj3, and thread 3 locks obj3 then asks for obj1. Each thread gets its first lock during its one-second work, then waits for a lock held by the next thread. The waits form a circle (thread 1 waits for thread 2, which waits for thread 3, which waits for thread 1), so no thread can continue and none of them releases its lock. The last messages printed are the three "acquiring lock" lines for the second objects, and nothing else happens after that.

2. Propose a solution to solve the identified problem.

The solution is to make all threads take the locks in the same global order, which removes the circular wait. In `NonDeadlockExample`, thread 3 now locks obj1 then obj3 instead of obj3 then obj1. All threads follow the order obj1, then obj2, then obj3, so a thread can only wait for a thread that holds a lock "after" its own, and a circle cannot form. Some threads still have to wait for others, but all three finish, and the program prints "All threads finished: no deadlock." after about 5 seconds.

### Deadlock detection

A `synchronized` block cannot time out, and a thread waiting to enter one cannot be interrupted. To detect a stuck thread, `AcessSharedObjects` starts a small watchdog thread just before each lock attempt:

- If the lock has not been obtained after 10 seconds, the watchdog prints a warning saying which thread is waiting and for which object.
- If the lock has still not been obtained after 20 seconds, the watchdog prints an error saying the thread is starving because of a deadlock, and ends the program with exit code 1.
- As soon as the lock is obtained, the watchdog is cancelled, so normal runs are not affected. The watchdog is a daemon thread, so it never keeps the program running by itself.

With `DeadlockExample`, all three threads print the warning after about 10 seconds, and the program is ended after about 20 seconds instead of hanging forever. With `NonDeadlockExample`, the program finishes normally without any warning.
