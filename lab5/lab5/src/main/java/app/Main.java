package app;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        SyncList syncList = new SyncList();

//        for (Integer i = 0; i < 10; i++) {
//            syncList.add(i);
//        }

        List<Thread> addingThreads = new ArrayList<>();
        List<Thread> containsThreads = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Integer finalI = i;

            var thread = new Thread(() -> {
                syncList.add(finalI);
            });

            var containsThread = new Thread(() -> {
                boolean contains = syncList.contains(finalI);
                System.out.println("Does list contain value " + finalI + "?: " + contains);
            });

            addingThreads.add(thread);
            containsThreads.add(containsThread);
        }

        for (Thread thread : addingThreads) {
            thread.start();
            thread.join();
        }

        for (Thread thread : containsThreads) {
            thread.start();
            thread.join();
        }

        System.out.println(syncList.contains(2));
        System.out.println(syncList.contains(11));
    }
}
