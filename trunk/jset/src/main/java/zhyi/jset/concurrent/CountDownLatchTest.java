package zhyi.jset.concurrent;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CountDownLatchTest {
    public static void main(String[] args) {
        final int count = 5;
        final CountDownLatch cdl = new CountDownLatch(count);

        for (int i = 0; i < count; i++) {
            new Thread() {
                @Override
                public void run() {
                    long id = Thread.currentThread().getId();
                    System.out.println("Thread [" + id + "] started.");

                    try {
                        TimeUnit.SECONDS.sleep(new Random().nextInt(3) + 2);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    System.out.println("Thread [" + id + "] finished computing.");
                    cdl.countDown();
                }
            }.start();
        }

        System.out.println("Waiting for all threads to finish computing...");
        try {
            cdl.await();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        System.out.println("All threads finished computing.");
    }
}
