package app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Fork {
    private final int id;
    private boolean isUsed = false;

    Fork(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public boolean isUsed() {
        return this.isUsed;
    }

    public void use() {
        this.isUsed = true;
    }

    public void release() {
        this.isUsed = false;
    }
}

abstract class AbstractPhilosopher implements Runnable {
    public static final int SLEEP_TIME = 500;
    protected final Random random = new Random();
    protected final int id;
    protected final List<Fork> forks;

    AbstractPhilosopher(int id, List<Fork> forks) {
        this.id = id;
        this.forks = forks;
    }

    protected void think() throws InterruptedException {
        System.out.println(String.format("Philosopher %d is thinking...", this.id));
        Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));
        System.out.println(String.format("Philosopher %d stops thinking...", this.id));
    }

    @Override
    public void run() {
        try {
            // start with random delay for each philosopher
            Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));

            for (int i = 0; i < 100; i++) {
                this.eat();
                this.think();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    abstract public void eat() throws InterruptedException;
}

class Philosopher1 extends AbstractPhilosopher {
    Philosopher1(int id, List<Fork> forks) {
        super(id, forks);
    }

    // v1: use left fork first
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


class Philosopher2 implements Runnable {
    public static final int SLEEP_TIME = 500;
    protected final Random random = new Random();
    protected final int id;
    protected final List<Lock> forks;

    Philosopher2(int id, List<Lock> forks) {
        this.id = id;
        this.forks = forks;
    }

    protected void think() throws InterruptedException {
        System.out.println(String.format("Philosopher %d is thinking...", this.id));
        Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));
        System.out.println(String.format("Philosopher %d stops thinking...", this.id));
    }

    // v1: use left fork first
    public void eat() throws InterruptedException {
        // use left fork first
        Lock firstFork = this.forks.get(this.id);
        Lock secondFork = this.forks.get((this.id + 1) % this.forks.size());

        if (this.random.nextInt(2) == 0) {
            Lock tmpFork = firstFork;
            firstFork = secondFork;
            secondFork = tmpFork;
        }

        firstFork.lock();
        System.out.println(String.format("Philosopher %d takes left fork", this.id));
        firstFork.unlock();

        secondFork.lock();
        System.out.println(String.format("Philosopher %d takes right fork", this.id));
        secondFork.unlock();
    }

    @Override
    public void run() {
        try {
            // start with random delay for each philosopher
            Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));

            for (int i = 0; i < 100; i++) {
                this.eat();
                this.think();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class Philosopher3 implements Runnable {
    public static final int SLEEP_TIME = 500;
    protected final Random random = new Random();
    protected final int id;
    protected final List<Semaphore> forks;

    Philosopher3(int id, List<Semaphore> forks) {
        this.id = id;
        this.forks = forks;
    }

    protected void think() throws InterruptedException {
        System.out.println(String.format("Philosopher %d is thinking...", this.id));
        Thread.sleep(this.random.nextInt(SLEEP_TIME));
        System.out.println(String.format("Philosopher %d stops thinking...", this.id));
    }

    // v1: use left fork first
    public void eat() throws InterruptedException {
        // use left fork first
        Semaphore firstFork = this.forks.get(this.id);
        Semaphore secondFork = this.forks.get((this.id + 1) % this.forks.size());
        String firstForkStr = "left";
        String secondForkStr = "right";


        if (this.id % 2 == 1) {
            Semaphore tmp = firstFork;
            firstFork = secondFork;
            secondFork = tmp;
        }

        firstFork.acquire();
        System.out.println(String.format("Philosopher %d takes %s fork", this.id));
        firstFork.release();

        secondFork.acquire();
        System.out.println(String.format("Philosopher %d takes %s fork", this.id));
        secondFork.release();
    }

    @Override
    public void run() {
        try {
            // start with random delay for each philosopher
            Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));

            for (int i = 0; i < 100; i++) {
                this.eat();
                this.think();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class Philosopher4 implements Runnable {
    public static final int SLEEP_TIME = 500;
    protected final Random random = new Random();
    protected final int id;
    protected final List<Lock> forks;

    Philosopher4(int id, List<Lock> forks) {
        this.id = id;
        this.forks = forks;
    }

    protected void think() throws InterruptedException {
        System.out.println(String.format("Philosopher %d is thinking...", this.id));
        Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));
        System.out.println(String.format("Philosopher %d stops thinking...", this.id));
    }

    // v1: use left fork first
    public void eat() throws InterruptedException {
        // use left fork first
        Lock firstFork = this.forks.get(this.id);
        Lock secondFork = this.forks.get((this.id + 1) % this.forks.size());

        firstFork.lock();
        secondFork.lock();

        System.out.println(String.format("Philosopher %d takes left fork", this.id));
        System.out.println(String.format("Philosopher %d takes right fork", this.id));

        firstFork.unlock();
        secondFork.unlock();
    }

    @Override
    public void run() {
        try {
            // start with random delay for each philosopher
            Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));

            for (int i = 0; i < 100; i++) {
                this.eat();
                this.think();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

public class Main {
    // v1
//    public static void main(String[] args) {
//        int forkCount = 5;
//        List<Fork> forks = new ArrayList<>(forkCount);
//        for (int i = 0; i < forkCount; i++) {
//            forks.add(new Fork(i));
//        }
//
//        List<AbstractPhilosopher> philosophers = new ArrayList<>(forkCount);
//        for (int i = 0; i < forkCount; i++) {
//            philosophers.add(new Philosopher1(i, forks));
//        }
//
//        for (AbstractPhilosopher philosopher : philosophers) {
//            new Thread(philosopher).start();
//        }
//    }

    // v2
//    public static void main(String[] args) {
//        int forkCount = 5;
//        List<Lock> forks = new ArrayList<>(forkCount);
//        for (int i = 0; i < forkCount; i++) {
//            forks.add(new ReentrantLock());
//        }
//
//        List<Philosopher2> philosophers = new ArrayList<>(forkCount);
//        for (int i = 0; i < forkCount; i++) {
//            philosophers.add(new Philosopher2(i, forks));
//        }
//
//        for (Philosopher2 philosopher : philosophers) {
//            new Thread(philosopher).start();
//        }
//    }

    // v4
    public static void main(String[] args) {

        Fork f = new Fork(1);
        int forkCount = 5;
        List<Lock> forks = new ArrayList<>(forkCount);
        for (int i = 0; i < forkCount; i++) {
            forks.add(new ReentrantLock());
        }

        List<Philosopher4> philosophers = new ArrayList<>(forkCount);
        for (int i = 0; i < forkCount; i++) {
            philosophers.add(new Philosopher4(i, forks));
        }

        for (Philosopher4 philosopher : philosophers) {
            new Thread(philosopher).start();
        }
    }
}
