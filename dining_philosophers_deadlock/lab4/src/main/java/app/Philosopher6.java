package app;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

// v6: eat in corridor if there is no space for current philosopher in the canteen
public class Philosopher6 extends AbstractPhilosopher {
    protected final List<Lock> forks;
    protected final Semaphore canteen;
    private Lock firstFork;
    private Lock secondFork;
    private boolean eatsInCanteen = false;

    Philosopher6(int id, List<Lock> forks, Semaphore canteen) {
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
        String eatsIn = "canteen";

        // try to acquire access to canteen
        eatsInCanteen = this.canteen.tryAcquire();

        if (!eatsInCanteen) {
            Lock tmp = firstFork;
            firstFork = secondFork;
            secondFork = tmp;

            String tmpStr = firstForkString;
            firstForkString = secondForkString;
            secondForkString = tmpStr;
            eatsIn = "corridor";
        }

        firstFork.lock();

        secondFork.lock();
    }

    @Override
    public void releaseForks() throws InterruptedException {
        firstFork.unlock();
        secondFork.unlock();

        if (eatsInCanteen) {
            this.canteen.release();
        }
    }
}
