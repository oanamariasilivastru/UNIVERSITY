package org.example;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyQueue<T> {
    private int MAX = 50;
    private final Lock lock;
    private final Condition notFull;
    private final Condition notEmpty;

    private AtomicInteger totalProducers;
    private final Queue<T> queue = new LinkedList<>();
    private boolean isFinished = false;

    public MyQueue(AtomicInteger totalProducers) {
        this.lock = new ReentrantLock();
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
        this.totalProducers = totalProducers;
    }

    public void finish(){
        lock.lock();
        try {
            isFinished = true;
            notEmpty.signalAll();
            notFull.signalAll();
            System.out.println("Queue marked as finished.");
        } finally {
            lock.unlock();
        }
    }


    public void push (T element) throws InterruptedException{
        lock.lock();
        try{
            while(queue.size() == MAX){
                System.out.println("Queue is full. Operator is waiting to enqueue.");
                notFull.await();
            }
            queue.add(element);
            notEmpty.signal();
            System.out.println("Enqueued: " + element + ". Queue size now: " + queue.size());
        } finally {
            lock.unlock();
        }
    }


    public T pop() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                if (isFinished && totalProducers.get() == 0) {
                    return null;
                }
                System.out.println("Queue is empty. Packaging Team is waiting to dequeue.");
                notEmpty.await();
            }
            T result = queue.remove();
            notFull.signal();
            System.out.println("Dequeued: " + result + ". Queue size now: " + queue.size());
            return result;
        } finally {
            lock.unlock();
        }
    }


    public int size() {
        lock.lock();
        try{
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

}
