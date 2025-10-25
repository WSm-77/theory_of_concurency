package app;

import java.util.List;
import java.util.concurrent.Semaphore;

// v3: asymmetric solution
public class Philosopher3 extends AbstractPhilosopher {
    protected final List<Semaphore> forks;

    Philosopher3(int id, List<Semaphore> forks) {
        super(id);
        this.forks = forks;
    }

    @Override
    public void eat() throws InterruptedException {
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
}
