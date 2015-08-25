package zhyi.jset.concurrent;

import java.util.Random;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

public class ExchangerTest {
    public static void main(String[] args) {
        final Exchanger<String> e = new Exchanger<>();

        new Thread() {
            @Override
            public void run() {
                long id = Thread.currentThread().getId();
                String s = "abc";
                System.out.println("Thread [" + id + "] is sending " + s + "...");

                try {
                    TimeUnit.SECONDS.sleep(new Random().nextInt(3) + 2);
                    System.out.println("Thread [" + id + "] received " + e.exchange(s) + "...");
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                long id = Thread.currentThread().getId();
                String s = "xyz";
                System.out.println("Thread [" + id + "] is sending " + s + "...");

                try {
                    TimeUnit.SECONDS.sleep(new Random().nextInt(3) + 2);
                    System.out.println("Thread [" + id + "] received " + e.exchange(s) + "...");
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }
}
