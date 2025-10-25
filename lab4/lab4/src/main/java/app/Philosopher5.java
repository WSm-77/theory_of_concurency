package app;

import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;

// v5: solution with mediator
public class Philosopher5 extends AbstractPhilosopher {
    protected final List<Lock> forks;
    protected Mediator5 mediator;
    private boolean isHalted = false;

    Philosopher5(int id, List<Lock> forks) {
        super(id);
        this.forks = forks;
    }

    public void setMediator(Mediator5 mediator) {
        this.mediator = mediator;
    }

    synchronized public void awake() {
        this.isHalted = false;

        System.out.println(String.format("Philosopher %d awakes", this.id));

        this.notify();
    }

    public void halt() {
        this.isHalted = true;

        System.out.println(String.format("Philosopher %d is halted", this.id));
    }

    public boolean isHalted() {
        return this.isHalted;
    }

    @Override
    public void eat() throws InterruptedException {
        Lock firstFork = this.forks.get(this.id);
        Lock secondFork = this.forks.get((this.id + 1) % this.forks.size());

        synchronized (this) {
            while (this.isHalted) {
                this.wait();
            }
        }

        firstFork.lock();
        System.out.println(String.format("Philosopher %d takes left fork", this.id));

        secondFork.lock();
        System.out.println(String.format("Philosopher %d takes right fork", this.id));

        System.out.println(String.format("Philosopher %d eats...", this.id));

        Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));

        System.out.println(String.format("Philosopher %d stops eating and releases forks", this.id));

        firstFork.unlock();
        secondFork.unlock();

        this.mediator.notifyForkRelease(this);
    }
}
