package app;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        SyncList syncList = new SyncList();

        List<Thread> addingThreads = new ArrayList<>();
        List<Thread> containsThreads = new ArrayList<>();
        List<Thread> removeThreads = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Integer finalI = i;

            var thread = new Thread(() -> {
                syncList.add(finalI);
            });

            var containsThread = new Thread(() -> {
                boolean contains = syncList.contains(finalI);
                System.out.println("Does list contain value " + finalI + "?: " + contains);
            });

            var removeThread = new Thread(() -> {
                boolean contains = syncList.remove(finalI);
                System.out.println("Can list remove value " + finalI + "?: " + contains);
            });

            addingThreads.add(thread);
            containsThreads.add(containsThread);
            removeThreads.add(removeThread);
        }

        for (Thread thread : addingThreads) {
            thread.start();
            thread.join();
        }

        for (Thread thread : containsThreads) {
            thread.start();
            thread.join();
        }

        for (Thread thread : removeThreads) {
            thread.start();
            thread.join();
        }

        System.out.println(syncList.contains(2));
        System.out.println(syncList.contains(11));
    }
}
