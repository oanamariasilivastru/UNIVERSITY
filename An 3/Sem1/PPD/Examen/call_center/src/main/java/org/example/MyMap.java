package org.example;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyMap {
    private final HashMap<Integer, List<Call>> map; // Cheia este id_agent
    private final Lock lock;

    public MyMap() {
        this.map = new HashMap<>();
        this.lock = new ReentrantLock();
    }

    public void add(Call value) {
        lock.lock();
        try {
            int key = value.getIdAgent();
            List<Call> list = map.computeIfAbsent(key, k -> {
                return new ArrayList<>();
            });
            list.add(value);
            list.sort(Comparator.comparingInt(Call::getDifficulty));
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return map.values().stream().mapToInt(List::size).sum();
        } finally {
            lock.unlock();
        }
    }

    public Map<Integer, List<Call>> getMap() {
        lock.lock();
        try {
            return new HashMap<>(map);
        } finally {
            lock.unlock();
        }
    }
}
