package app;

import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;

public class Philosopher4 extends AbstractPhilosopher {
    protected final List<Lock> forks;

    Philosopher4(int id, List<Lock> forks) {
        super(id);
        this.forks = forks;
    }

    @Override
    public void eat() throws InterruptedException {
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
}
