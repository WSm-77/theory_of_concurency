package app;

public class Mediator5 {
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
