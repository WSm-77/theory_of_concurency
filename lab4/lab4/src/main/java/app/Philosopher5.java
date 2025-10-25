package app;

import java.util.List;
import java.util.concurrent.locks.Lock;

// v5: solution with mediator
public class Philosopher5 extends AbstractPhilosopher {
    protected final List<Lock> forks;
    protected Mediator5 mediator;
    private boolean isHalted = false;
    private Lock firstFork;
    private Lock secondFork;

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
    public void acquireForks() throws InterruptedException {
        firstFork = this.forks.get(this.id);
        secondFork = this.forks.get((this.id + 1) % this.forks.size());

        synchronized (this) {
            while (this.isHalted) {
                this.wait();
            }
        }

        firstFork.lock();
        System.out.println(String.format("Philosopher %d takes left fork", this.id));

        secondFork.lock();
        System.out.println(String.format("Philosopher %d takes right fork", this.id));
    }

    @Override
    public void releaseForks() throws InterruptedException {
        firstFork.unlock();
        secondFork.unlock();

        this.mediator.notifyForkRelease(this);
    }
}
