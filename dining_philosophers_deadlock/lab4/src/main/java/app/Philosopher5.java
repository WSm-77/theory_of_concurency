package app;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

// v5: solution with mediator
public class Philosopher5 extends AbstractPhilosopher {
    protected final List<Lock> forks;
    protected final Semaphore canteen;
    private Lock firstFork;
    private Lock secondFork;

    Philosopher5(int id, List<Lock> forks, Semaphore canteen) {
        super(id);
        this.forks = forks;
        this.canteen = canteen;
    }

    @Override
    public void acquireForks() throws InterruptedException {
        // use left fork first
        firstFork = this.forks.get(this.id);
        secondFork = this.forks.get((this.id + 1) % this.forks.size());
        String firstForkString = "left";
        String secondForkString = "right";

        // acquire access to canteen
        this.canteen.acquire();

        firstFork.lock();

        secondFork.lock();
    }

    @Override
    public void releaseForks() throws InterruptedException {
        firstFork.unlock();
        secondFork.unlock();

        this.canteen.release();
    }
}
