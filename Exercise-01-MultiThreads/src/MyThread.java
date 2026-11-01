public class MyThread extends Thread {
    private int sleepTime;
    private int target;

    public MyThread(String name, int sleepTime, int target) {
        super(name);
        this.sleepTime = sleepTime;
        this.target = target;
    }

    @Override
    public void run() {
        for(int i=0; i<target;i++) {
            System.out.println(super.getName() + " is running: " + i);
        }
        try{
            Thread.sleep(sleepTime);
        }catch(InterruptedException ex){

        }
    }
}