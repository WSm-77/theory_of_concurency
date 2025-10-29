package app;

import java.util.concurrent.locks.Lock;

public class SyncList {
    public ListElem guardian = new ListElem();

    public boolean contains(Object value) {
        ListElem current = this.guardian;

        Lock currentReadLock =  current.lock.readLock();

        try {
            // lock current element
            currentReadLock.lock();

            while (current.next != null) {
                ListElem next = current.next;

                Lock nextReadLock = next.lock.readLock();

                // lock next element
                nextReadLock.lock();

                // unlock previous element
                currentReadLock.unlock();
                currentReadLock = nextReadLock;

                if (next.value.equals(value)) {
                    return true;
                }

                current = next;
                currentReadLock = current.lock.readLock();
            }
        }
        finally {
            currentReadLock.unlock();
        }

        return false;
    }

    public void add(Object value) {
        ListElem current = this.guardian;

        Lock currentReadLock =  current.lock.readLock();
//        Lock prevLock = currentReadLock;

        try {
            // lock current element
            currentReadLock.lock();

            while (current.next != null) {
                ListElem next = current.next;

                Lock nextReadLock = next.lock.readLock();

                // lock next element
                nextReadLock.lock();

                // unlock previous element
                currentReadLock.unlock();
                currentReadLock = nextReadLock;

                current = next;
                currentReadLock = current.lock.readLock();
            }
        }
        finally {
            currentReadLock.unlock();
            System.out.println("Val:  " + value + " added to list");
        }

        Lock currentWriteLock = current.lock.writeLock();
        currentWriteLock.lock();
        current.next = new ListElem(value);
        currentWriteLock.unlock();
    }
}
