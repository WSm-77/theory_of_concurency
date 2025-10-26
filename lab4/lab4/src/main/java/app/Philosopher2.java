package app;

import java.util.List;
import java.util.concurrent.locks.Lock;

// v2: use random fork first
public class Philosopher2 extends AbstractPhilosopher {
    protected final List<Lock> forks;
    private Lock firstFork;
    private Lock secondFork;

    Philosopher2(int id, List<Lock> forks) {
        super(id);
        this.forks = forks;
    }

    @Override
    public void acquireForks() throws InterruptedException {
        firstFork = this.forks.get(this.id);
        secondFork = this.forks.get((this.id + 1) % this.forks.size());

        if (this.random.nextInt(2) == 0) {
            Lock tmpFork = firstFork;
            firstFork = secondFork;
            secondFork = tmpFork;
        }

        firstFork.lock();
//        System.out.println(String.format("Philosopher %d takes left fork", this.id));

        secondFork.lock();
//        System.out.println(String.format("Philosopher %d takes right fork", this.id));
    }

    @Override
    public void releaseForks() throws InterruptedException {
        firstFork.unlock();
        secondFork.unlock();

//        System.out.println(String.format("Philosopher %d stops eating and releases forks", this.id));
    }
}
