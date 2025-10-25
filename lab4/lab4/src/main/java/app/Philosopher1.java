package app;

import java.util.List;

// v1: use left fork first
public class Philosopher1 extends AbstractPhilosopher {
    private final List<Fork> forks;

    Philosopher1(int id, List<Fork> forks) {
        super(id);
        this.forks = forks;
    }

    @Override
    public void eat() throws InterruptedException {
        // use left fork first
        Fork leftFork = this.forks.get(this.id);

        synchronized (leftFork) {
            while (leftFork.isUsed()) {
                leftFork.wait();
            }

            System.out.println(String.format("Philosopher %d takes left fork", this.id));
            leftFork.use();
            leftFork.notifyAll();
        }

        Fork rightFork = this.forks.get((this.id + 1) % this.forks.size());

        synchronized (rightFork) {
            while (rightFork.isUsed()) {
                rightFork.wait();
            }

            System.out.println(String.format("Philosopher %d takes right fork", this.id));
            rightFork.use();
            rightFork.notifyAll();
        }

        // eating process...
        System.out.println(String.format("Philosopher %d is eating...", this.id));
        Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));
        System.out.println(String.format("Philosopher %d stops eating and releases forks", this.id));

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
