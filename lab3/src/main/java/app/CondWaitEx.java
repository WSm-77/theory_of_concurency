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
    private final Buffer _buf;

    Producer(Buffer buf) {
        this._buf = buf;
    }


    public void run() {
        for (int i = 0; i < 100; ++i) {
//            try {
//                TimeUnit.MILLISECONDS.wait(this.sleepTime);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }

            synchronized (_buf) {
                int waitCnt = 0;
                while (_buf.isFull()) {
                    try {
                        System.out.println(String.format("%d. Waiting for buffer to not be full", waitCnt++));
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                _buf.put(1);
                System.out.println(String.format("Producing -> buff size=%d", _buf.getBuffCount()));
            }

            _buf.put(i);
        }
    }
}

class Consumer extends Person {
    private final Buffer _buf;

    Consumer(Buffer buf) {
        this._buf = buf;
    }

    public void run() {
        for (int i = 0; i < 100; ++i) {
//            try {
//                TimeUnit.MILLISECONDS.wait(this.sleepTime);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }

            synchronized (_buf) {
                int waitCnt = 0;

                while (_buf.isEmpty()) {
                    try {
                        System.out.println(String.format("%d. Waiting for buffer to not be full", waitCnt++));
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                System.out.println(String.format("Consuming -> buff size=%d",_buf.get()));
            }
        }
    }
}

class Buffer {
    private final int buffSize;
    private int buffCount;

    Buffer (int size) {
        this.buffSize = size;
    }

    synchronized public void put(int i) {
        if (!this.isFull()) {
            this.buffCount += i;
        }

        this.notifyAll();
    }

    synchronized public int get() {
        if (!this.isEmpty()) {
            this.notifyAll();
            return 0;
        }

        int ret = this.buffCount--;
        this.notifyAll();

        return ret;
    }

    synchronized public int getBuffCount() {
        return this.buffCount;
    }

    public synchronized boolean isFull() {
        return this.buffCount >= this.buffSize;
    }

    public synchronized boolean isEmpty() {
        return this.buffCount == 0;
    }
}

public class CondWaitEx {
    public static void main(String[] args) {
        int bufferSize = 5;
        Buffer buffer = new Buffer(bufferSize);

        Producer producer = new Producer(buffer);
        Consumer consumer = new Consumer(buffer);

        try {
            producer.start();
            consumer.start();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
