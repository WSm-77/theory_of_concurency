package app;

import java.util.Random;
import java.util.concurrent.Callable;

abstract public class AbstractPhilosopher implements Callable<Double> {
    public static final int CYCLES_COUNT = 10;
    public static final int SLEEP_TIME = 10;
    protected final Random random = new Random();
    protected final int id;

    AbstractPhilosopher(int id) {
        this.id = id;
    }

    protected void think() throws InterruptedException {
        System.out.println(String.format("Philosopher %d is thinking...", this.id));
        Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));
        System.out.println(String.format("Philosopher %d stops thinking...", this.id));
    }

    protected void eat() throws InterruptedException {
        // eating process...
        System.out.println(String.format("Philosopher %d is eating...", this.id));
        Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));
        System.out.println(String.format("Philosopher %d stops eating and releases forks", this.id));
    }

    @Override
    public Double call() {
        Double totalWaitTime = 0.0;

        try {
            // start with random delay for each philosopher
            Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));

            for (int i = 0; i < AbstractPhilosopher.CYCLES_COUNT; i++) {
                // measure waiting time
                long startTime = System.nanoTime();

                this.acquireForks();

                long elapsedTime = System.nanoTime() - startTime;
                totalWaitTime += elapsedTime;

                this.eat();
                this.releaseForks();
                this.think();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return totalWaitTime / AbstractPhilosopher.CYCLES_COUNT;
    }

    abstract public void acquireForks() throws InterruptedException;
    abstract public void releaseForks() throws InterruptedException;
}
