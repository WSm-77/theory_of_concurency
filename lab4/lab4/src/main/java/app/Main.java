package app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static List<Double> execute(int sol, int forkCount) {
        System.out.println("Sol: " + sol);
        List<Double> meanExecutionTimes;

        try (ExecutorService executor = Executors.newFixedThreadPool(6)) {
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

                    Semaphore canteen = new Semaphore(forkCount - 1);

                    List<AbstractPhilosopher> philosophers = new ArrayList<>(forkCount);
                    for (int i = 0; i < forkCount; i++) {
                        philosophers.add(new Philosopher5(i, forks, canteen));
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
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return meanExecutionTimes;
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
        final int forkCount = 100;
        final List<Integer> solutionsWithoutDeadlock = List.of(2, 3, 4, 5, 6);

//        {
//            final int sol = 5; // change this value to run different variants
//            Main.execute(sol, forkCount);
//        }

        // Collect results per solution
        Map<Integer, List<Double>> resultsPerSolution = new LinkedHashMap<>();

        for (int sol : solutionsWithoutDeadlock) {
            List<Double> meanExecutionTimes = Main.execute(sol, forkCount);

            meanExecutionTimes.forEach((time) -> System.out.printf("Executing %f ms.\n", time));

            resultsPerSolution.put(sol, meanExecutionTimes);
        }

        // Save combined CSV: columns = solutions (header = solution numbers), rows = measurements per philosopher
        try {
            saveCombinedCsv(new ArrayList<>(resultsPerSolution.keySet()), resultsPerSolution, "results_combined.csv");
            System.out.println("Saved combined results to results_combined.csv");
        } catch (IOException e) {
            System.err.println("Failed to save combined CSV: " + e.getMessage());
        }
    }

    /**
     * Save combined results into a single CSV file where each column is a solution (header = solution number)
     * and each row contains the measurement for philosopher index i across solutions.
     */
    private static void saveCombinedCsv(List<Integer> solutions, Map<Integer, List<Double>> resultsPerSolution, String fileName) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            // header: index,sol1,sol2,... (use solution numbers as headers)
            StringBuilder header = new StringBuilder();
            header.append("philosopher_index");
            for (int sol : solutions) {
                header.append(",").append(sol);
            }
            header.append("\n");
            bw.write(header.toString());

            // determine number of rows (assume equal lengths; use max length defensively)
            int maxRows = 0;
            for (int sol : solutions) {
                List<Double> list = resultsPerSolution.get(sol);
                if (list != null && list.size() > maxRows) {
                    maxRows = list.size();
                }
            }

            for (int i = 0; i < maxRows; i++) {
                StringBuilder row = new StringBuilder();
                row.append(i);
                for (int sol : solutions) {
                    List<Double> list = resultsPerSolution.get(sol);
                    row.append(",");
                    if (list != null && i < list.size()) {
                        row.append(list.get(i));
                    } else {
                        row.append("");
                    }
                }
                row.append("\n");
                bw.write(row.toString());
            }
        }
    }
}
