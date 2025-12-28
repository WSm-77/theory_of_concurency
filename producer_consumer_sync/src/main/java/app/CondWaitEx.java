package app;

import java.util.ArrayList;
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
    private final int _id;

    Person () {
        final Random rand = new Random();
        this.sleepTime = rand.nextInt(20) * 50;
        this._id = UniqId.newId();
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
                        _buf.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                _buf.put(1);
                System.out.println(String.format("[%d] Producing -> buff size=%d", this._id, _buf.getBuffCount()));
                _buf.notifyAll();
            }
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
                        System.out.println(String.format("%d. Waiting for buffer to not be empty", waitCnt++));
                        _buf.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                System.out.println(String.format("[%d] Consuming -> buff size=%d", this._id, _buf.get()));
                _buf.notifyAll();
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
        this.buffCount += i;
//        if (!this.isFull()) {
//            this.buffCount += i;
//        }
//
//        this.notifyAll();
    }

//    synchronized public int get() {
//        if (!this.isEmpty()) {
//            this.notifyAll();
//            return 0;
//        }
//
//        int ret = this.buffCount--;
//        this.notifyAll();
//
//        return ret;
//    }

    synchronized public int get() {
//        if (!this.isEmpty()) {
//            this.notifyAll();
//            return 0;
//        }

        int ret = this.buffCount--;
//        this.notifyAll();

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

        int producersCnt = 5;
        int consumersCnt = 5;

        Producer[] producers = new Producer[producersCnt];
        Consumer[] consumers = new Consumer[consumersCnt];

        try {
            for (int i = 0; i < producersCnt; ++i) {
                producers[i] = new Producer(buffer);
                producers[i].start();
            }
            for (int i = 0; i < consumersCnt; ++i) {
                consumers[i] = new Consumer(buffer);
                consumers[i].start();
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
