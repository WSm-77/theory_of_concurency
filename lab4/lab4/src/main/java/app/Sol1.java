package app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class Fork {
    private final int id;
    private boolean isUsed = false;

    Fork(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public boolean isUsed() {
        return this.isUsed;
    }

    public void use() {
        this.isUsed = true;
    }

    public void release() {
        this.isUsed = false;
    }
}

abstract class AbstractPhilosopher implements Runnable {
    public static final int SLEEP_TIME = 500;
    protected final Random random = new Random();
    protected final int id;
    protected final List<Fork> forks;

    AbstractPhilosopher(int id, List<Fork> forks) {
        this.id = id;
        this.forks = forks;
    }

    protected void think() throws InterruptedException {
        System.out.println(String.format("Philosopher %d is thinking...", this.id));
        Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));
        System.out.println(String.format("Philosopher %d stops thinking...", this.id));
    }

    @Override
    public void run() {
        try {
            // start with random delay for each philosopher
            Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));

            for (int i = 0; i < 100; i++) {
                this.eat();
                this.think();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    abstract public void eat() throws InterruptedException;
}

class Philosopher1 extends AbstractPhilosopher {
    Philosopher1(int id, List<Fork> forks) {
        super(id, forks);
    }

    // v1: use left fork first
    @Override
    public void eat() throws InterruptedException {
        // use left fork first
        Fork leftFork = this.forks.get(this.id);

        synchronized (leftFork) {
            while (leftFork.isUsed()) {
                leftFork.wait();
            }

            System.out.println(String.format("Philosopher %d takes left fork", this.id));
            leftFork.use();
            leftFork.notify();
        }

        Fork rightFork = this.forks.get((this.id + 1) % this.forks.size());

        synchronized (rightFork) {
            while (rightFork.isUsed()) {
                rightFork.wait();
            }

            System.out.println(String.format("Philosopher %d takes right fork", this.id));
            rightFork.use();
            rightFork.notify();
        }

        // eating process...
        System.out.println(String.format("Philosopher %d is eating...", this.id));
        Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));
        System.out.println(String.format("Philosopher %d stops eating and releases forks", this.id));

        synchronized (leftFork) {
            leftFork.release();
            leftFork.notifyAll();
        }

        synchronized (rightFork) {
            rightFork.release();
            rightFork.notifyAll();
        }
    }
}

public class Sol1 {
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
}
