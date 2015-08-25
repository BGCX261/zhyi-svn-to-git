package zhyi.jset.concurrent;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class CyclicBarrierTest {
    public static void main(String[] args) {
        final int parties = 5;
        final CyclicBarrier cb = new CyclicBarrier(parties, new Runnable() {
            @Override
            public void run() {
                System.out.println("All threads finished computing.");
            }
        });

        for (int i = 0; i < parties; i++) {
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

                    System.out.println("Thread [" + id + "] is waiting for other threads to finish computing...");
                    try {
                        cb.await();
                    } catch (InterruptedException | BrokenBarrierException ex) {
                        ex.printStackTrace();
                    }
                }
            }.start();
        }
    }
}
