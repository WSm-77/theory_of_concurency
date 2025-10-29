package app;

import java.util.concurrent.locks.Lock;

public class SyncList {
    public ListElem guardian = new ListElem();

    public boolean contains(Object value) {
        ListElem head = this.guardian;

        Lock headReadLock = head.lock.readLock();
        headReadLock.lock();

        while (head.next != null) {
            ListElem next = head.next;

            Lock nextReadLock = next.lock.readLock();
            nextReadLock.lock();

            headReadLock.unlock();

            if (next.value.equals(value)) {
                nextReadLock.unlock();
                return true;
            }

            head = next;
        }

        headReadLock.unlock();

        return false;
    }

    public void add(Object value) {
        ListElem head = this.guardian;

        Lock headReadLock =  head.lock.readLock();
        Lock prevLock = headReadLock;

        try {
            // lock current element
            prevLock.lock();

            while (head.next != null) {
                ListElem next = head.next;

                Lock nextReadLock = next.lock.readLock();

                // lock current element
                nextReadLock.lock();

                // unlock previous element
                prevLock.unlock();
                prevLock = head.lock.readLock();

                head = next;
                headReadLock = head.lock.readLock();
            }
        }
        finally {
            headReadLock.unlock();
        }

        Lock headWriteLock = head.lock.writeLock();
        headWriteLock.lock();
        head.next = new ListElem(value);
        headWriteLock.unlock();
    }
}
