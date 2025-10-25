package app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void execute(int sol) {
        switch (sol) {
            case 1: // v1: Fork objects + Philosopher1
            {
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
                break;
            }

            case 2: // v2: Locks + Philosopher2
            {
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
                break;
            }

            case 3: // v3: Semaphores + Philosopher3
            {
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
                break;
            }

            case 4: // v4: tryLock + Philosopher4
            {
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
                break;
            }

            case 5: // v5: Mediator + Philosopher5
            {
                int forkCount = 5;
                List<Lock> forks = new ArrayList<>(forkCount);
                for (int i = 0; i < forkCount; i++) {
                    forks.add(new ReentrantLock());
                }

                List<Philosopher5> philosophers = new ArrayList<>(forkCount);
                for (int i = 0; i < forkCount; i++) {
                    philosophers.add(new Philosopher5(i, forks));
                }

                Mediator5 mediator = new Mediator5(philosophers.get(forkCount - 1));
                for (Philosopher5 philosopher : philosophers) {
                    philosopher.setMediator(mediator);
                    new Thread(philosopher).start();
                }
                break;
            }

            case 6: // v6: canteen semaphore + Philosopher6
            {
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
                break;
            }

            default:
                System.err.println("Unknown solution number: " + sol + " (expected 1..6)");
        }
    }

   public static void main(String[] args) {
        // Choose which solution to run (1..6)
        final int sol = 6; // change this value to run different variants

        Main.execute(sol);
    }
}
