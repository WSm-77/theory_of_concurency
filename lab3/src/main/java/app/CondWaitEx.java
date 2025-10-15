package app;

import java.util.concurrent.TimeUnit;
import java.util.Random;




class UniqId {
    private static int id = 0;

    synchronized public static int newId () {
        return id++;
    }
}

abstract class Person extends Thread {
    final int sleepTime;

    Person () {
        final Random rand = new Random();
        this.sleepTime = rand.nextInt(20) * 50;
    }
}

class Producer extends Person {
    private Buffer _buf;


    public void run() {
        for (int i = 0; i < 100; ++i) {
            try {
                TimeUnit.MILLISECONDS.wait(this.sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            _buf.put(i);
        }
    }
}

class Consumer extends Person {
    private Buffer _buf;

    public void run() {
        for (int i = 0; i < 100; ++i) {
            try {
                TimeUnit.MILLISECONDS.wait(this.sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println(_buf.get());
        }
    }
}

class Buffer {
    public void put(int i) {
        // Implementation needed
    }

    public int get() {
        // Implementation needed
        return 0;
    }
}

public class CondWaitEx {
    public static void main(String[] args) {
        // Implementation needed
    }
}
