package app;

import java.util.List;
import java.util.concurrent.Semaphore;

// v3: asymmetric solution
public class Philosopher3 extends AbstractPhilosopher {
    protected final List<Semaphore> forks;
    private Semaphore firstFork;
    private Semaphore secondFork;

    Philosopher3(int id, List<Semaphore> forks) {
        super(id);
        this.forks = forks;
    }

    @Override
    public void acquireForks() throws InterruptedException {
        firstFork = this.forks.get(this.id);
        secondFork = this.forks.get((this.id + 1) % this.forks.size());
        String firstForkStr = "left";
        String secondForkStr = "right";

        if (this.id % 2 == 1) {
            Semaphore tmp = firstFork;
            firstFork = secondFork;
            secondFork = tmp;
        }

        firstFork.acquire();

        secondFork.acquire();
    }

    @Override
    public void releaseForks() {
        firstFork.release();
        secondFork.release();
    }
}
