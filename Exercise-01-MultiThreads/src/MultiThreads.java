public class MultiThreads {
    public static void main(String[] args) throws Exception {
        experimentwithThreads(3, 50, 5);
    }

    public static void experimentwithThreads(int num_threads, int sleepTime, int target){
        MyThread[] threads = new MyThread[num_threads];
        for(int i = 0; i < num_threads; i++){
            threads[i] = new MyThread("Thread " + (i + 1), sleepTime, target);
            threads[i].start();
        }
        for(int i = 0; i < num_threads; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
