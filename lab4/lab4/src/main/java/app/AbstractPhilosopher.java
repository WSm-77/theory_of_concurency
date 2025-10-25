package app;

import java.util.List;
import java.util.Random;

abstract public class AbstractPhilosopher implements Runnable {
    public static final int SLEEP_TIME = 500;
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
    public void run() {
        try {
            // start with random delay for each philosopher
            Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));

            for (int i = 0; i < 100; i++) {
                this.acquireForks();
                this.eat();
                this.releaseForks();
                this.think();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    abstract public void acquireForks() throws InterruptedException;
    abstract public void releaseForks() throws InterruptedException;
}
