package app;

import java.util.List;
import java.util.concurrent.locks.Lock;

public class Philosopher4 extends AbstractPhilosopher {
    protected final List<Lock> forks;
    private Lock firstFork;
    private Lock secondFork;

    Philosopher4(int id, List<Lock> forks) {
        super(id);
        this.forks = forks;
    }

    @Override
    public void acquireForks() throws InterruptedException {
        firstFork = this.forks.get(this.id);
        secondFork = this.forks.get((this.id + 1) % this.forks.size());

        while (true) {
            firstFork.lock();

            if (secondFork.tryLock()) {
                break;
            } else {
                firstFork.unlock();
            }
        }
    }

    @Override
    public void releaseForks() throws InterruptedException {
        firstFork.unlock();
        secondFork.unlock();
    }
}
