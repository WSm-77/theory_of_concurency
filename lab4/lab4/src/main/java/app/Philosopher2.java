package app;

import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;

// v2: use random fork first
public class Philosopher2 extends AbstractPhilosopher {
    protected final List<Lock> forks;

    Philosopher2(int id, List<Lock> forks) {
        super(id);
        this.forks = forks;
    }

    @Override
    public void eat() throws InterruptedException {
        Lock firstFork = this.forks.get(this.id);
        Lock secondFork = this.forks.get((this.id + 1) % this.forks.size());

        if (this.random.nextInt(2) == 0) {
            Lock tmpFork = firstFork;
            firstFork = secondFork;
            secondFork = tmpFork;
        }

        firstFork.lock();
        System.out.println(String.format("Philosopher %d takes left fork", this.id));

        secondFork.lock();
        System.out.println(String.format("Philosopher %d takes right fork", this.id));

        System.out.println(String.format("Philosopher %d eats...", this.id));

        Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));

        firstFork.unlock();
        secondFork.unlock();

        System.out.println(String.format("Philosopher %d stops eating and releases forks", this.id));
    }
}
