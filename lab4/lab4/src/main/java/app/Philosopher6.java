package app;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

// v6: eat in corridor if there is no space for current philosopher in the canteen
public class Philosopher6 extends AbstractPhilosopher {
    protected final List<Lock> forks;
    protected final Semaphore canteen;

    Philosopher6(int id, List<Lock> forks, Semaphore canteen) {
        super(id);
        this.forks = forks;
        this.canteen = canteen;
    }

    @Override
    public void eat() throws InterruptedException {
        // use left fork first
        Lock firstFork = this.forks.get(this.id);
        Lock secondFork = this.forks.get((this.id + 1) % this.forks.size());
        String firstForkString = "left";
        String secondForkString = "right";
        String eatsIn = "canteen";

        // try to acquire access to canteen
        boolean eatsInCanteen = this.canteen.tryAcquire();

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
        System.out.println(String.format("Philosopher %d takes %s fork", this.id, firstForkString));

        secondFork.lock();
        System.out.println(String.format("Philosopher %d takes %s fork", this.id, secondForkString));

        System.out.println(String.format("Philosopher %d eats in %s...", this.id, eatsIn));

        Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));

        System.out.println(String.format("Philosopher %d stops eating and releases forks", this.id));

        firstFork.unlock();
        secondFork.unlock();

        if (eatsInCanteen) {
            this.canteen.release();
        }
    }
}
