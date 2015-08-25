package zhyi.jset.concurrent;

import java.util.Random;
import java.util.concurrent.Phaser;

public class PhaserTest {
    public static void main(String[] args) throws Exception {
//        final Phaser p = new Phaser() {
//            @Override
//            protected boolean onAdvance(int phase, int registeredParties) {
//                System.out.println("Phase " + (phase + 1) + " completed.");
//                if (phase == 2 || registeredParties == 0) {
//                    System.out.println("All Phases completed.");
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        };
//        for (int i = 0; i < 5; i++) {
//            final int party = i;
//            System.out.println("Registering party " + party + "...");
//            p.register();
//            new Thread() {
//                @Override
//                public void run() {
//                    if (new Random().nextInt(10) % 2 == 0) {
//                        System.out.println("Party " + party + " deregistered.");
//                        p.arriveAndDeregister();
//                        return;
//                    }
//
//                    for (int phase = 1; !p.isTerminated(); phase++) {
//                        System.out.println("Party " + party + " entered phase "
//                                + phase +  " and is waiting for others...");
//                        p.arriveAndAwaitAdvance();
//                    }
//                }
//            }.start();
//        }
        final Phaser parent = new Phaser(1) {
            @Override
            protected boolean onAdvance(int phase, int registeredParties) {
                System.out.println("parent = " + phase);
                return phase == 5;
            }
        };
        for (int i = 0; i < 4; i++) {
            final Phaser child = new Phaser(parent);
            child.register();
            new Thread() {
                @Override
                public void run() {
                    while (!parent.isTerminated()) {
                        System.out.println(Thread.currentThread());
                        child.arriveAndAwaitAdvance();
                    }
                }
            }.start();
        }
        parent.arriveAndDeregister();
    }
}
