package zhyi.jset.concurrent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorServiceTest {
    public static void main(String[] args) {
        String dir = "C:/Program Files";
        System.out.println("Counting files in [" + dir + "]...");
        long t = System.currentTimeMillis();

        final ExecutorService es = Executors.newCachedThreadPool();
        class CountingTask implements Callable<Integer> {
            private Path dir;

            private CountingTask(Path dir) {
                this.dir = dir;
            }

            @Override
            public Integer call() throws Exception {
                try {
                    int count = 0;
                    List<CountingTask> subTasks = new ArrayList<>();
                    for (Path subPath : Files.newDirectoryStream(dir)) {
                        if (Files.isDirectory(subPath, LinkOption.NOFOLLOW_LINKS)) {
                            subTasks.add(new CountingTask(subPath));
                        } else {
                            count++;
                        }
                    }
                    for (Future<Integer> f : es.invokeAll(subTasks)) {
                        count += f.get();
                    }
                    return count;
                } catch (IOException ex) {
                    return 0;
                }
            }
        }

        try {
            int count = es.submit(new CountingTask(Paths.get(dir))).get();
            es.shutdown();
            System.out.println("Finished in " + (System.currentTimeMillis() - t) + "ms.");
            System.out.println("File count is " + count + ".");
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
    }
}
