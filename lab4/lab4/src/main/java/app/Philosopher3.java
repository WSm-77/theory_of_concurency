package app;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Philosopher3 implements Runnable {
    public static final int SLEEP_TIME = 500;
    protected final Random random = new Random();
    protected final int id;
    protected final List<Semaphore> forks;

    Philosopher3(int id, List<Semaphore> forks) {
        this.id = id;
        this.forks = forks;
    }

    protected void think() throws InterruptedException {
        System.out.println(String.format("Philosopher %d is thinking...", this.id));
        Thread.sleep(this.random.nextInt(SLEEP_TIME));
        System.out.println(String.format("Philosopher %d stops thinking...", this.id));
    }

    // v1: use left fork first
    public void eat() throws InterruptedException {
        // use left fork first
        Semaphore firstFork = this.forks.get(this.id);
        Semaphore secondFork = this.forks.get((this.id + 1) % this.forks.size());
        String firstForkStr = "left";
        String secondForkStr = "right";


        if (this.id % 2 == 1) {
            Semaphore tmp = firstFork;
            firstFork = secondFork;
            secondFork = tmp;
        }

        firstFork.acquire();
        System.out.println(String.format("Philosopher %d takes %s fork", this.id, firstForkStr));


        secondFork.acquire();
        System.out.println(String.format("Philosopher %d takes %s fork", this.id, secondForkStr));

        System.out.println(String.format("Philosopher %d eats...", this.id));

        Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));

        System.out.println(String.format("Philosopher %d stops eating and releases forks", this.id));

        firstFork.release();
        secondFork.release();
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
