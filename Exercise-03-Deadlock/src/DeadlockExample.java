public class DeadlockExample {
    public static void main(String[] args)
        throws InterruptedException {
        Object obj1 = new Object();
        Object obj2 = new Object();
        Object obj3 = new Object();
        Thread thread1 = new Thread(new AcessSharedObjects(obj1, obj2), "thread1");
        Thread thread2 = new Thread(new AcessSharedObjects(obj2, obj3), "thread2");
        Thread thread3 = new Thread(new AcessSharedObjects(obj3, obj1), "thread3");
        thread1.start();
        Thread.sleep(500);
        thread2.start();
        Thread.sleep(500);
        thread3.start();
    }
}