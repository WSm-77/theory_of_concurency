package app;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ListElem {
    public ListElem next = null;
//    public ListElem prev = null;
    public final ReadWriteLock lock = new ReentrantReadWriteLock();
    public Object value = null;

    public ListElem() {}

    public ListElem(Object value) {
        this(value, null);
    }

    public ListElem(Object value, ListElem next) {
        this.value = value;
        this.next = next;
    }

    public void setNext(ListElem next) {
        this.next = next;
    }

    public ListElem getNext() {
        return this.next;
    }

    public ReadWriteLock getLock() {
        return lock;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
