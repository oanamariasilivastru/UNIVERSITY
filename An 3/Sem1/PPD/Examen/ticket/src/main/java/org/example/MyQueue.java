package org.example;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Generic thread-safe queue with "producer-consumer" semantics.
 * Uses a Lock+Condition mechanism (no busy-waiting).
 */
public class MyQueue<T> {
    private final int MAX = 500;  // or any large capacity you want
    private final Lock lock;
    private final Condition notFull;
    private final Condition notEmpty;

    private final AtomicInteger totalProducers;
    private final Queue<T> queue = new LinkedList<>();

    public MyQueue(AtomicInteger totalProducers) {
        this.lock = new ReentrantLock();
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
        this.totalProducers = totalProducers;
    }

    public AtomicInteger getTotalProducers() {
        return totalProducers;
    }

    /**
     * Signals that a thread finished producing for this queue.
     * We just signalAll so that any waiting threads can reevaluate conditions.
     */
    public void finish() {
        lock.lock();
        try {
            notEmpty.signalAll();
            notFull.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Push an element into the queue, waiting if it is full.
     */
    public void push(T element) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == MAX) {
                notFull.await();
            }
            queue.add(element);
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Pop an element from the queue, waiting if it is empty but producers still exist.
     * If there are no producers and the queue is empty, returns null.
     */
    public T pop() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                // If no more producers exist for this queue, we must stop
                if (totalProducers.get() == 0) {
                    return null;
                }
                notEmpty.await();
            }
            T result = queue.remove();
            notFull.signal();
            return result;
        } finally {
            lock.unlock();
        }
    }

    /**
     * True if the queue is either non-empty OR we still have producers.
     * If totalProducers == 0 and queue is empty, then no more data will ever arrive.
     */
    public boolean hasData() {
        lock.lock();
        try {
            if (totalProducers.get() == 0) {
                return !queue.isEmpty();
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the current size of the queue safely.
     */
    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }
}
