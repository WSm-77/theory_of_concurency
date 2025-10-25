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

    // v2: use random fork first
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

        secondFork.lock();
        System.out.println(String.format("Philosopher %d takes right fork", this.id));

        System.out.println(String.format("Philosopher %d eats...", this.id));

        Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));

        firstFork.unlock();
        secondFork.unlock();

        System.out.println(String.format("Philosopher %d stops eating and releases forks", this.id));
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
        System.out.println(String.format("Philosopher %d takes %s fork", this.id, firstForkStr));


        secondFork.acquire();
        System.out.println(String.format("Philosopher %d takes %s fork", this.id, secondForkStr));

        System.out.println(String.format("Philosopher %d eats...", this.id));

        Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));

        System.out.println(String.format("Philosopher %d stops eating and releases forks", this.id));

        firstFork.release();
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

class Mediator5 {
    private Philosopher5 haltedPhilosopher = null;

    Mediator5(Philosopher5 philosopherToHalt) {
        philosopherToHalt.halt();
        this.haltedPhilosopher = philosopherToHalt;
    }

    public void notifyForkRelease(Philosopher5 philosopher) {
        philosopher.halt();
        this.haltedPhilosopher.awake();
        this.haltedPhilosopher = philosopher;
    }
}

class Philosopher5 implements Runnable {
    public static final int SLEEP_TIME = 500;
    protected final Random random = new Random();
    protected final int id;
    protected final List<Lock> forks;
    protected Mediator5 mediator;
    private boolean isHalted = false;

    Philosopher5(int id, List<Lock> forks) {
        this.id = id;
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

class Mediator6 {
    private final int philosophersCnt;
    private final Semaphore canteen;

    Mediator6(int philosophersCnt) {
        this.philosophersCnt = philosophersCnt;
        this.canteen = new Semaphore(this.philosophersCnt - 1);
    }
}

class Philosopher6 implements Runnable {
    protected final Random random = new Random();
    protected final int id;
    protected final List<Lock> forks;
    protected final Semaphore canteen;

    Philosopher6(int id, List<Lock> forks, Semaphore canteen) {
        this.id = id;
        this.forks = forks;
        this.canteen = canteen;
    }

    protected void think() throws InterruptedException {
        System.out.println(String.format("Philosopher %d is thinking...", this.id));
        Thread.sleep(this.random.nextInt(AbstractPhilosopher.SLEEP_TIME));
        System.out.println(String.format("Philosopher %d stops thinking...", this.id));
    }

    // v6: eat in corridor if there is no space for current philosopher in the canteen
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

    //   v3
//    public static void main(String[] args) {
//        int forkCount = 5;
//        List<Semaphore> forks = new ArrayList<>(forkCount);
//        for (int i = 0; i < forkCount; i++) {
//            forks.add(new Semaphore(1));
//        }
//
//        List<Philosopher3> philosophers = new ArrayList<>(forkCount);
//        for (int i = 0; i < forkCount; i++) {
//            philosophers.add(new Philosopher3(i, forks));
//        }
//
//        for (Philosopher3 philosopher : philosophers) {
//            new Thread(philosopher).start();
//        }
//    }

    // v4
//    public static void main(String[] args) {
//        int forkCount = 5;
//        List<Lock> forks = new ArrayList<>(forkCount);
//        for (int i = 0; i < forkCount; i++) {
//            forks.add(new ReentrantLock());
//        }
//
//        List<Philosopher4> philosophers = new ArrayList<>(forkCount);
//        for (int i = 0; i < forkCount; i++) {
//            philosophers.add(new Philosopher4(i, forks));
//        }
//
//        for (Philosopher4 philosopher : philosophers) {
//            new Thread(philosopher).start();
//        }
//    }

    // v5
//    public static void main(String[] args) {
//        int forkCount = 5;
//        List<Lock> forks = new ArrayList<>(forkCount);
//
//
//        for (int i = 0; i < forkCount; i++) {
//            forks.add(new ReentrantLock());
//        }
//
//        List<Philosopher5> philosophers = new ArrayList<>(forkCount);
//        for (int i = 0; i < forkCount; i++) {
//            philosophers.add(new Philosopher5(i, forks));
//        }
//
//        Mediator5 mediator = new Mediator5(philosophers.getLast());
//
//        for (Philosopher5 philosopher : philosophers) {
//            philosopher.setMediator(mediator);
//            new Thread(philosopher).start();
//        }
//    }

    // v6
    public static void main(String[] args) {
        int forkCount = 5;
        List<Lock> forks = new ArrayList<>(forkCount);
        Semaphore canteen = new Semaphore(forkCount - 1);

        for (int i = 0; i < forkCount; i++) {
            forks.add(new ReentrantLock());
        }

        List<Philosopher6> philosophers = new ArrayList<>(forkCount);
        for (int i = 0; i < forkCount; i++) {
            philosophers.add(new Philosopher6(i, forks, canteen));
        }

        for (Philosopher6 philosopher : philosophers) {
            new Thread(philosopher).start();
        }
    }
}
