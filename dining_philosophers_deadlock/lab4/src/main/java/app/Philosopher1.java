package app;

import java.util.List;

// v1: use left fork first
public class Philosopher1 extends AbstractPhilosopher {
    private final List<Fork> forks;
    private Fork leftFork;
    private Fork rightFork;

    Philosopher1(int id, List<Fork> forks) {
        super(id);
        this.forks = forks;
    }

    @Override
    public void acquireForks() throws InterruptedException {
        // use left fork first
        this.leftFork = this.forks.get(this.id);

        synchronized (leftFork) {
            while (leftFork.isUsed()) {
                leftFork.wait();
            }

//            System.out.println(String.format("Philosopher %d takes left fork", this.id));
            leftFork.use();
            leftFork.notifyAll();
        }

        this.rightFork = this.forks.get((this.id + 1) % this.forks.size());

        synchronized (rightFork) {
            while (rightFork.isUsed()) {
                rightFork.wait();
            }

//            System.out.println(String.format("Philosopher %d takes right fork", this.id));
            rightFork.use();
            rightFork.notifyAll();
        }
    }

    @Override
    public void releaseForks() throws InterruptedException {
        synchronized (leftFork) {
            leftFork.release();
            leftFork.notifyAll();
        }

        synchronized (rightFork) {
            rightFork.release();
            rightFork.notifyAll();
        }
    }
}
