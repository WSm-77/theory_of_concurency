package app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    // v6
    public static void main(String[] args) {
        int forkCount = 5;
        List<Lock> forks = new ArrayList<>(forkCount);
        Semaphore canteen = new Semaphore(forkCount - 1);

        for (int i = 0; i < forkCount; i++) {
            forks.add(new ReentrantLock());
        }

        List<Philosopher6> philosophers = new ArrayList<>(forkCount);
        for (int i = 0; i < forkCount; i++) {
            philosophers.add(new Philosopher6(i, forks, canteen));
        }

        for (Philosopher6 philosopher : philosophers) {
            new Thread(philosopher).start();
        }
    }
}
