package zhyi.jset.concurrent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinTest {
    public static void main(String[] args) {
        String dir = "C:/Program Files";
        System.out.println("Counting files in [" + dir + "]...");
        long t = System.currentTimeMillis();

        class CountingTask extends RecursiveTask<Integer> {
            private Path dir;

            private CountingTask(Path dir) {
                this.dir = dir;
            }

            @Override
            protected Integer compute() {
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
                    for (CountingTask subTask : invokeAll(subTasks)) {
                        count += subTask.join();
                    }
                    return count;
                } catch (IOException ex) {
                    return 0;
                }
            }
        }

        ForkJoinPool fjp = new ForkJoinPool();
        CountingTask ct = new CountingTask(Paths.get(dir));
        Integer count = fjp.invoke(ct);
        System.out.println("Finished in " + (System.currentTimeMillis() - t) + "ms.");
        System.out.println("File count is " + count + ".");
    }
}
