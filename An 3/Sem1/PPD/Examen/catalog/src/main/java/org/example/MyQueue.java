//package org.example;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Queue;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.locks.Condition;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//
//public class MyQueue<T> {
//    private final int MAX = 50;
//    private final Lock lock;
//    private final Condition notFull;
//    private final Condition notEmpty;
//    private final AtomicInteger totalProducers;
//    private final Queue<T> queue = new LinkedList<>();
//
//    public MyQueue(AtomicInteger totalProducers) {
//        this.lock = new ReentrantLock();
//        this.notFull = lock.newCondition();
//        this.notEmpty = lock.newCondition();
//        this.totalProducers = totalProducers;
//    }
//
//    public void finish(){
//        lock.lock();
//        notEmpty.signalAll();
//        notFull.signalAll();
//        lock.unlock();
//    }
//
//    public void push (T element) throws InterruptedException{
//        lock.lock();
//        try{
//            while(queue.size() == MAX){
//                notFull.await();
//                //not full e pentru producatori - ii pune sa astepte
//            }
//            queue.add(element);
//            notEmpty.signal();
//            // indica ca s a adaugat in coada ceva si semnleaza
//            // notifica consumatorii ca pot consuma
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    public T pop() throws InterruptedException {
//        T result;
//        lock.lock();
//        try {
//            while (queue.isEmpty()) {
//                //daca coada este coala, consumatorul nu are ce prelua
//                //si trebuie sa astepte
//
//                // variabila atomica care tine evidenta cati producatori
//                //sunt activi
//
//                if (totalProducers.get() == 0) {
//                    return null;
//                }
//                //consumatorul ramane in stare de asteptare
//                notEmpty.await();
//            }
//            //scoatem un element
//            result = queue.remove();
//            //notificam producatorul ca nu mai este plina coada
//            //si ca poate adauga elemente in continuare
//            notFull.signal();
//            return result;
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    public boolean hasData(){
//        lock.lock();
//        try{
//            //daca au terminat toti producerii, asta avem in coada;
//            if(totalProducers.get() == 0){
//                return !queue.isEmpty();
//            }
//            //daca n au terminat, mai pot fi adaugate pe viitor
//            return true;
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    public int size() {
//        lock.lock();
//        try{
//            return queue.size();
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    public List<T> getAllElements() {
//        lock.lock();
//        try {
//            return new ArrayList<>(queue);
//        } finally {
//            lock.unlock();
//        }
//    }
//
//}
package org.example;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class MyQueue<T> {
    private final int MAX = 50;
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

    public void finish() {
        lock.lock();
        try {
            notEmpty.signalAll();
            notFull.signalAll();
        } finally {
            lock.unlock();
        }
    }

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


    public T pop() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
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


    public int size() {
        lock.lock();
        try {
            return queue.size();
        } finally {
            lock.unlock();
        }
    }

    public List<T> getAllElements() {
        lock.lock();
        try {
            return new ArrayList<>(queue);
        } finally {
            lock.unlock();
        }
    }
}
