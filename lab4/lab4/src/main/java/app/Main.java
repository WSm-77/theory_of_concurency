package app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void execute(int sol, int forkCount) {
        System.out.println("Sol: " + sol);

        try (ExecutorService executor = Executors.newFixedThreadPool(6)) {
            List<Double> meanExecutionTimes;
            List<Future<Double>> meanExecutionTimeFutures = new ArrayList<>();

            switch (sol) {
                case 1: // v1: Fork objects + Philosopher1
                {
                    List<Fork> forks = new ArrayList<>(forkCount);
                    for (int i = 0; i < forkCount; i++) {
                        forks.add(new Fork(i));
                    }

                    List<AbstractPhilosopher> philosophers = new ArrayList<>(forkCount);
                    for (int i = 0; i < forkCount; i++) {
                        philosophers.add(new Philosopher1(i, forks));
                    }

                    meanExecutionTimes = Main.getMeanExecutionTime(philosophers, executor);
                    break;
                }

                case 2: // v2: Locks + Philosopher2
                {
                    List<Lock> forks = new ArrayList<>(forkCount);
                    for (int i = 0; i < forkCount; i++) {
                        forks.add(new ReentrantLock());
                    }

                    List<AbstractPhilosopher> philosophers = new ArrayList<>(forkCount);
                    for (int i = 0; i < forkCount; i++) {
                        philosophers.add(new Philosopher2(i, forks));
                    }

                    meanExecutionTimes = Main.getMeanExecutionTime(philosophers, executor);
                    break;
                }

                case 3: // v3: Semaphores + Philosopher3
                {
                    List<Semaphore> forks = new ArrayList<>(forkCount);
                    for (int i = 0; i < forkCount; i++) {
                        forks.add(new Semaphore(1));
                    }

                    List<AbstractPhilosopher> philosophers = new ArrayList<>(forkCount);
                    for (int i = 0; i < forkCount; i++) {
                        philosophers.add(new Philosopher3(i, forks));
                    }

                    meanExecutionTimes = Main.getMeanExecutionTime(philosophers, executor);
                    break;
                }

                case 4: // v4: tryLock + Philosopher4
                {
                    List<Lock> forks = new ArrayList<>(forkCount);
                    for (int i = 0; i < forkCount; i++) {
                        forks.add(new ReentrantLock());
                    }

                    List<AbstractPhilosopher> philosophers = new ArrayList<>(forkCount);
                    for (int i = 0; i < forkCount; i++) {
                        philosophers.add(new Philosopher4(i, forks));
                    }

                    meanExecutionTimes = Main.getMeanExecutionTime(philosophers, executor);
                    break;
                }

                case 5: // v5: Mediator + Philosopher5
                {
                    List<Lock> forks = new ArrayList<>(forkCount);
                    for (int i = 0; i < forkCount; i++) {
                        forks.add(new ReentrantLock());
                    }

                    List<AbstractPhilosopher> philosophers = new ArrayList<>(forkCount);
                    for (int i = 0; i < forkCount; i++) {
                        philosophers.add(new Philosopher5(i, forks));
                    }

                    Mediator5 mediator = new Mediator5((Philosopher5) philosophers.get(forkCount - 1));
                    for (AbstractPhilosopher philosopher : philosophers) {
                        ((Philosopher5)philosopher).setMediator(mediator);
                    }

                    meanExecutionTimes = Main.getMeanExecutionTime(philosophers, executor);
                    break;
                }

                case 6: // v6: canteen semaphore + Philosopher6
                {
                    List<Lock> forks = new ArrayList<>(forkCount);
                    Semaphore canteen = new Semaphore(forkCount - 1);

                    for (int i = 0; i < forkCount; i++) {
                        forks.add(new ReentrantLock());
                    }

                    List<AbstractPhilosopher> philosophers = new ArrayList<>(forkCount);
                    for (int i = 0; i < forkCount; i++) {
                        philosophers.add(new Philosopher6(i, forks, canteen));
                    }

                    meanExecutionTimes = Main.getMeanExecutionTime(philosophers, executor);
                    break;
                }

                default:
                    System.err.println("Unknown solution number: " + sol + " (expected 1..6)");
                    throw new RuntimeException("Unknown solution number: " + sol + " (expected 1..6)");
            }

            meanExecutionTimes.forEach((time) -> System.out.printf("Executing %f ms.\n", time / 1_000_000.0));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Double> getMeanExecutionTime(List<AbstractPhilosopher> philosophers, ExecutorService executor) throws ExecutionException, InterruptedException {
        List<Double> meanExecutionTimes = new ArrayList<>();
        List<Future<Double>> meanExecutionTimeFutures = new ArrayList<>();

        for (AbstractPhilosopher philosopher : philosophers) {
            meanExecutionTimeFutures.add(executor.submit(philosopher));
        }

        for (Future<Double> meanExecTimeFuture : meanExecutionTimeFutures) {
            meanExecutionTimes.add(meanExecTimeFuture.get());
        }

        return meanExecutionTimes;
    }

    public static void main(String[] args) {
        // Choose which solution to run (1..6)
        final int sol = 6; // change this value to run different variants
        final int forkCount = 5;

        Main.execute(sol, forkCount);
    }
}
