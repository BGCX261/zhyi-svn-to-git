package zhyi.jset.concurrent;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CompletionServiceTest {
    public static void main(String[] args) {
        final int count = 5;
        ExecutorService es = Executors.newCachedThreadPool();
        CompletionService<Integer> cs = new ExecutorCompletionService<>(es);
        for (int i = 0; i < count; i++) {
            cs.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    int r = new Random().nextInt(3) + 2;
                    TimeUnit.SECONDS.sleep(r);
                    return r;
                }
            });
        }
        for (int i = 0; i < count; i++) {
            try {
                System.out.println(cs.take().get());
            } catch (InterruptedException | ExecutionException ex) {
                ex.printStackTrace();
            }
        }
        es.shutdown();
    }
}
