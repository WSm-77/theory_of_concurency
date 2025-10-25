package app;

import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;

public class Philosopher4 implements Runnable {
    public static final int SLEEP_TIME = 500;
    protected final Random random = new Random();
    protected final int id;
    protected final List<Lock> forks;

    Philosopher4(int id, List<Lock> forks) {
        this.id = id;
        this.forks = forks;
    }

    protected void think() throws InterruptedException {
        System.out.println(String.format("Philosopher %d is thinking...", this.id));
        Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));
        System.out.println(String.format("Philosopher %d stops thinking...", this.id));
    }

    // v1: use left fork first
    public void eat() throws InterruptedException {
        // use left fork first
        Lock firstFork = this.forks.get(this.id);
        Lock secondFork = this.forks.get((this.id + 1) % this.forks.size());

        while (true) {
            firstFork.lock();

            if (secondFork.tryLock()) {
                System.out.println(String.format("Philosopher %d takes left fork", this.id));
                System.out.println(String.format("Philosopher %d takes right fork", this.id));

                System.out.println(String.format("Philosopher %d eats...", this.id));

                Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));

                System.out.println(String.format("Philosopher %d stops eating and releases forks", this.id));

                firstFork.unlock();
                secondFork.unlock();

                break;
            } else {
                firstFork.unlock();
            }
        }
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
}
