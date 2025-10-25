package app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
// v1
   public static void main(String[] args) {
       int forkCount = 5;
       List<Fork> forks = new ArrayList<>(forkCount);
       for (int i = 0; i < forkCount; i++) {
           forks.add(new Fork(i));
       }

       List<AbstractPhilosopher> philosophers = new ArrayList<>(forkCount);
       for (int i = 0; i < forkCount; i++) {
           philosophers.add(new Philosopher1(i, forks));
       }

       for (AbstractPhilosopher philosopher : philosophers) {
           new Thread(philosopher).start();
       }
   }

    // v2
   public static void main(String[] args) {
       int forkCount = 5;
       List<Lock> forks = new ArrayList<>(forkCount);
       for (int i = 0; i < forkCount; i++) {
           forks.add(new ReentrantLock());
       }

       List<Philosopher2> philosophers = new ArrayList<>(forkCount);
       for (int i = 0; i < forkCount; i++) {
           philosophers.add(new Philosopher2(i, forks));
       }

       for (Philosopher2 philosopher : philosophers) {
           new Thread(philosopher).start();
       }
   }

    //   v3
   public static void main(String[] args) {
       int forkCount = 5;
       List<Semaphore> forks = new ArrayList<>(forkCount);
       for (int i = 0; i < forkCount; i++) {
           forks.add(new Semaphore(1));
       }

       List<Philosopher3> philosophers = new ArrayList<>(forkCount);
       for (int i = 0; i < forkCount; i++) {
           philosophers.add(new Philosopher3(i, forks));
       }

       for (Philosopher3 philosopher : philosophers) {
           new Thread(philosopher).start();
       }
   }

    // v4
   public static void main(String[] args) {
       int forkCount = 5;
       List<Lock> forks = new ArrayList<>(forkCount);
       for (int i = 0; i < forkCount; i++) {
           forks.add(new ReentrantLock());
       }

       List<Philosopher4> philosophers = new ArrayList<>(forkCount);
       for (int i = 0; i < forkCount; i++) {
           philosophers.add(new Philosopher4(i, forks));
       }

       for (Philosopher4 philosopher : philosophers) {
           new Thread(philosopher).start();
       }
   }

    // v5
   public static void main(String[] args) {
       int forkCount = 5;
       List<Lock> forks = new ArrayList<>(forkCount);


       for (int i = 0; i < forkCount; i++) {
           forks.add(new ReentrantLock());
       }

       List<Philosopher5> philosophers = new ArrayList<>(forkCount);
       for (int i = 0; i < forkCount; i++) {
           philosophers.add(new Philosopher5(i, forks));
       }

       Mediator5 mediator = new Mediator5(philosophers.getLast());

       for (Philosopher5 philosopher : philosophers) {
           philosopher.setMediator(mediator);
           new Thread(philosopher).start();
       }
   }

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
